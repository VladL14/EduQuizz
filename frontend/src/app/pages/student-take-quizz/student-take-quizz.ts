import { Component, OnInit, Inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { QuizService } from '../../services/quizz';
import { Quiz } from '../../interfaces/quizz';
import { RequestType, Question, QuestionTestCase } from '../../interfaces/question';
import { from } from 'rxjs';
import { concatMap, finalize, map, toArray } from 'rxjs/operators';

@Component({
  selector: 'app-student-take-quizz',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './student-take-quizz.html',
  styleUrl: './student-take-quizz.css'
})
export class StudentTakeQuizz implements OnInit {
  quizId: number = 0;
  studentId: number = 0;
  quiz: Quiz | null = null;
  isLoading = true;
  errorMessage = '';
  RequestType = RequestType;
  gridAnswers: { [questionId: number]: Set<number> } = {};
  textAnswers: { [questionId: number]: string } = {};
  consoleOutputs: { [questionId: number]: string } = {};
  isRunning: { [questionId: number]: boolean } = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quizService: QuizService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      const userString = localStorage.getItem('currentUser');
      if (userString) this.studentId = JSON.parse(userString).id;

      this.route.paramMap.subscribe(params => {
        const id = params.get('id');
        if (id) {
          this.quizId = Number(id);
          this.loadQuiz();
        }
      });
    } else {
      this.isLoading = false;
      this.errorMessage = 'Aplicația nu rulează în browser.';
    }
  }

  loadQuiz() {
    this.isLoading = true;
    this.errorMessage = '';
    this.quizService.getQuizById(this.quizId).subscribe({
      next: (data) => {
        if (!data) {
          this.quiz = null;
          this.errorMessage = 'Nu am putut încărca testul.';
          this.isLoading = false;
          return;
        }
        const questions = Array.isArray(data.questions) ? data.questions : [];
        this.quiz = { ...data, questions };
        questions.forEach((q: Question) => {
          if (q.type === RequestType.GRID && !Array.isArray(q.options)) {
            q.options = [];
          }
          if (q.type === RequestType.CODE && !Array.isArray(q.testCases)) {
            q.testCases = [];
          }
          if (q.type === RequestType.GRID) {
            this.gridAnswers[q.id] = new Set<number>();
          } else {
            this.textAnswers[q.id] = '';
          }
        });
        if (this.studentId) {
          this.quizService.startQuiz(this.quizId, this.studentId).subscribe({
            error: () => {
              this.errorMessage = 'Nu am putut porni tentativa pentru acest test.';
            }
          });
        }
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = "Eroare la încărcarea testului sau testul nu mai este disponibil.";
      }
    });
  }
  toggleOption(questionId: number, optionId: number, event: any) {
    const checked = event.target.checked;
    if (checked) {
      this.gridAnswers[questionId].add(optionId);
    } else {
      this.gridAnswers[questionId].delete(optionId);
    }
  }
  runCode(questionId: number) {
    const code = this.textAnswers[questionId];
    const question = this.quiz?.questions.find((item) => item.id === questionId);
    const testCases = question?.testCases ?? [];

    if (!code) return;
    if (testCases.length === 0) {
      this.consoleOutputs[questionId] = "Nu există teste definite pentru această întrebare.";
      return;
    }

    this.isRunning[questionId] = true;
    this.consoleOutputs[questionId] = "Compiling & Running...";

    from(testCases)
      .pipe(
        concatMap((testCase: QuestionTestCase, index: number) =>
          this.quizService
            .runCode({ code, input: testCase.input, expectedOutput: testCase.expectedOutput })
            .pipe(
              map((res) => {
                const header = `Test ${index + 1}`;
                const verdict = res.success ? `✅ ${res.message || 'Success'}` : `❌ ${res.message}`;
                return `${header}\n${verdict}`;
              })
            )
        ),
        toArray(),
        finalize(() => {
          this.isRunning[questionId] = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (results) => {
          this.consoleOutputs[questionId] = results.join('\n\n');
          this.cdr.detectChanges();
        },
        error: () => {
          this.consoleOutputs[questionId] = "Eroare de conexiune la compilator.";
          this.cdr.detectChanges();
        }
      });
  }
  submitQuiz() {
    if (!confirm("Ești sigur că vrei să trimiți testul? Nu vei mai putea reveni.")) return;
    const responses = [];
    for (const qId in this.gridAnswers) {
      responses.push({
        questionId: Number(qId),
        selectedOptionIds: Array.from(this.gridAnswers[qId]),
        textAnswer: ''
      });
    }
    for (const qId in this.textAnswers) {
      responses.push({
        questionId: Number(qId),
        selectedOptionIds: [],
        textAnswer: this.textAnswers[qId]
      });
    }

    const payload = {
      studentId: this.studentId,
      responses: responses
    };

    this.quizService.submitQuiz(this.quizId, payload).subscribe({
      next: () => {
        alert("Test trimis cu succes!");
        this.router.navigate(['/student']);
      },
      error: (err) => alert("Eroare la trimiterea testului.")
    });
  }
}
