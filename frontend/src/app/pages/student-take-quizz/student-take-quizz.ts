import { Component, OnInit, OnDestroy, Inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core'; // 1. Adauga OnDestroy
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
export class StudentTakeQuizz implements OnInit, OnDestroy { // 2. Implementeaza OnDestroy
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

  // --- VARIABILE TIMER (NOI) ---
  timerString: string = "--:--:--";
  private timerInterval: any;
  isTimeCritical: boolean = false;
  // -----------------------------

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quizService: QuizService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      try {
        const userString = localStorage.getItem('currentUser');
        if (userString) {
          const user = JSON.parse(userString);
          this.studentId = user.id;
        }
      } catch (e) {
        console.error("Eroare localStorage", e);
      }

      this.route.paramMap.subscribe(params => {
        const id = params.get('id');
        if (id) {
          this.quizId = Number(id);
          this.loadQuiz();
        } else {
          this.errorMessage = 'ID-ul testului lipsește.';
          this.isLoading = false;
        }
      });
    } else {
      this.isLoading = false;
    }
  }

  // 3. Curățăm timer-ul când plecăm de pe pagină
  ngOnDestroy() {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  loadQuiz() {
    this.isLoading = true;
    this.errorMessage = '';

    this.quizService.getQuizById(this.quizId)
      .pipe(
        finalize(() => {
          // Nu oprim isLoading aici, așteptăm și startQuiz
          // Dar dacă startQuiz eșuează, trebuie gestionat
        })
      )
      .subscribe({
        next: (data) => {
          if (!data) {
            this.errorMessage = 'Testul nu a fost găsit.';
            this.isLoading = false;
            return;
          }

          const questions = Array.isArray(data.questions) ? data.questions : [];
          this.quiz = { ...data, questions };

          // Inițializare structuri de răspuns
          this.initializeAnswers(questions);

          // 4. PORNIM TESTUL PE SERVER ȘI PRIMIM ORA DE START
          if (this.studentId) {
            this.quizService.startQuiz(this.quizId, this.studentId).subscribe({
              next: (attempt) => {
                // Backend-ul trebuie să returneze obiectul Attempt cu "startTime"
                this.startTimer(attempt.startTime, this.quiz?.timeLimit || 0);
                this.isLoading = false;
                this.cdr.detectChanges();
              },
              error: (e) => {
                console.error("Eroare start quiz", e);
                this.isLoading = false;
                // Putem lăsa utilizatorul să continue, dar fără timer corect sincronizat
                // Sau afișăm eroare fatală
              }
            });
          } else {
            this.isLoading = false;
          }
        },
        error: (err) => {
          this.errorMessage = "Eroare la încărcarea testului.";
          this.isLoading = false;
        }
      });
  }

  // Helper pentru inițializarea răspunsurilor
  initializeAnswers(questions: any[]) {
    questions.forEach((q: any) => {
      // Fix tipuri
      const rawType = q.type as any;
      if (rawType === 'GRID' || rawType === RequestType.GRID) {
        q.type = RequestType.GRID;
        this.gridAnswers[q.id] = new Set<number>();
      } else if (rawType === 'TEXT' || rawType === RequestType.TEXT) {
        q.type = RequestType.TEXT;
        this.textAnswers[q.id] = '';
      } else {
        q.type = RequestType.CODE;
        this.textAnswers[q.id] = '';
      }
    });
  }

  // 5. LOGICA DE TIMER
  startTimer(startTimeStr: string, limitMinutes: number) {
    if (!startTimeStr || !limitMinutes) return;

    const startTime = new Date(startTimeStr).getTime();
    const deadline = startTime + (limitMinutes * 60 * 1000);

    this.timerInterval = setInterval(() => {
      const now = new Date().getTime();
      const distance = deadline - now;

      // a. Timpul a expirat
      if (distance < 0) {
        clearInterval(this.timerInterval);
        this.timerString = "00:00:00";
        this.submitQuiz(true); // true = forțat
        return;
      }

      // b. Calcul ceas
      const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((distance % (1000 * 60)) / 1000);

      this.timerString =
        (hours > 0 ? (hours < 10 ? "0" + hours : hours) + ":" : "") +
        (minutes < 10 ? "0" + minutes : minutes) + ":" +
        (seconds < 10 ? "0" + seconds : seconds);

      // c. Alertă roșie sub 2 minute
      this.isTimeCritical = distance < (2 * 60 * 1000);
      this.cdr.detectChanges(); // Update UI la fiecare secundă

    }, 1000);
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

    this.isRunning[questionId] = true;
    this.consoleOutputs[questionId] = "Compiling & Running...\n";

    from(testCases).pipe(
      concatMap((testCase: QuestionTestCase, index: number) =>
        this.quizService.runCode({ code, input: testCase.input, expectedOutput: testCase.expectedOutput })
          .pipe(map((res) => {
            const verdict = res.success ? `✅ Succes` : `❌ ${res.message}`;
            return `Test ${index + 1}: ${verdict}`;
          }))
      ),
      toArray(),
      finalize(() => {
        this.isRunning[questionId] = false;
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: (results) => {
        this.consoleOutputs[questionId] = results.join('\n');
        this.cdr.detectChanges();
      },
      error: () => {
        this.consoleOutputs[questionId] = "Eroare compilator.";
        this.cdr.detectChanges();
      }
    });
  }

  // 6. Modificat submitQuiz să accepte parametrul 'force'
  submitQuiz(force: boolean = false) {
    if (!force && !confirm("Ești sigur că vrei să trimiți testul?")) return;

    // Dacă e forțat (timp expirat), nu mai întrebăm, doar afișăm un mesaj scurt
    if (force) {
      alert("Timpul a expirat! Răspunsurile vor fi trimise automat.");
    }

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

    this.quizService.submitQuiz(this.quizId, responses, this.studentId).subscribe({
      next: () => {
        if (!force) alert("Test trimis cu succes!");
        this.router.navigate(['/student']);
      },
      error: (err) => {
        console.error(err);
        if (!force) alert("Eroare la trimiterea testului.");
      }
    });
  }
}
