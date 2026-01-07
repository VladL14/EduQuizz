import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { QuizService } from '../../services/quizz';
import { Quiz } from '../../interfaces/quizz';
import { RequestType, Question } from '../../interfaces/question';

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
  RequestType = RequestType;
  gridAnswers: { [questionId: number]: Set<number> } = {};
  textAnswers: { [questionId: number]: string } = {};
  consoleOutputs: { [questionId: number]: string } = {};
  customInputs: { [questionId: number]: string } = {};
  isRunning: { [questionId: number]: boolean } = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quizService: QuizService,
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
    }
  }

  loadQuiz() {
    this.quizService.getQuizById(this.quizId).subscribe({
      next: (data) => {
        const questions = Array.isArray(data.questions) ? data.questions : [];
        this.quiz = { ...data, questions };
        questions.forEach((q: Question) => {
          if (q.type === RequestType.GRID && !Array.isArray(q.options)) {
            q.options = [];
          }
          if (q.type === RequestType.GRID) {
            this.gridAnswers[q.id] = new Set<number>();
          } else {
            this.textAnswers[q.id] = '';
          }
        });
      },
      error: (err) => alert("Eroare la încărcarea testului sau testul nu mai este disponibil.")
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
    const input = this.customInputs[questionId] || '';

    if (!code) return;

    this.isRunning[questionId] = true;
    this.consoleOutputs[questionId] = "Compiling & Running...";

    this.quizService.runCode({ code, input }).subscribe({
      next: (res) => {
        this.isRunning[questionId] = false;
        if (res.error) {
          this.consoleOutputs[questionId] = `Eroare:\n${res.error}`;
        } else {
          this.consoleOutputs[questionId] = `Output:\n${res.output}`;
        }
      },
      error: (err) => {
        this.isRunning[questionId] = false;
        this.consoleOutputs[questionId] = "Eroare de conexiune la compilator.";
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
