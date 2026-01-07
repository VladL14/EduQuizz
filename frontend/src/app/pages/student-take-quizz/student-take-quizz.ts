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
  
  // Expunem Enum-ul pentru a fi folosit în HTML (*ngIf="q.type === RequestType.GRID")
  RequestType = RequestType;
  
  // Stocarea răspunsurilor
  gridAnswers: { [questionId: number]: Set<number> } = {};
  textAnswers: { [questionId: number]: string } = {};
  
  // Stocarea output-ului consolei pentru Python
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
    console.log("--> ngOnInit started");

    if (isPlatformBrowser(this.platformId)) {
      // 1. Identificăm studentul logat
      try {
        const userString = localStorage.getItem('currentUser');
        if (userString) {
          const user = JSON.parse(userString);
          this.studentId = user.id;
        }
      } catch (e) {
        console.error("--> Eroare la citirea userului din localStorage", e);
      }

      // 2. Luăm ID-ul testului din URL
      this.route.paramMap.subscribe(params => {
        const id = params.get('id'); // Asigură-te că în routing ai 'quiz/:id'
        console.log("--> Quiz ID from URL:", id);
        
        if (id) {
          this.quizId = Number(id);
          this.loadQuiz();
        } else {
          this.errorMessage = 'ID-ul testului lipsește din URL.';
          this.isLoading = false;
        }
      });
    } else {
      this.isLoading = false; 
    }
  }

  loadQuiz() {
    this.isLoading = true;
    this.errorMessage = '';
    console.log("--> Requesting Quiz ID:", this.quizId);

    this.quizService.getQuizById(this.quizId)
      .pipe(
        // 'finalize' garantează că spinner-ul dispare ORICE AR FI (succes sau eroare)
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges(); // Forțăm actualizarea UI
          console.log("--> Loading finished (Spinner stopped)");
        })
      )
      .subscribe({
        next: (data) => {
          console.log("--> JSON Primit:", data);

          if (!data) {
            this.errorMessage = 'Testul nu a fost găsit sau este gol.';
            return;
          }

          // Atribuim datele imediat
          const questions = Array.isArray(data.questions) ? data.questions : [];
          this.quiz = { ...data, questions };
          
          // Procesăm întrebările (tipuri, inițializări răspunsuri)
          try {
            questions.forEach((q: Question) => {
              
              // --- FIX PENTRU TS2367 ---
              // Folosim 'as any' pentru a permite compararea String-ului venit din Backend cu Enum-ul
              const rawType = q.type as any; 

              const isGrid = rawType === 'GRID' || rawType === RequestType.GRID;
              const isText = rawType === 'TEXT' || rawType === RequestType.TEXT;
              const isCode = rawType === 'CODE' || rawType === RequestType.CODE;
              
              // Normalizăm tipul pentru HTML
              if (isGrid) q.type = RequestType.GRID;
              if (isText) q.type = RequestType.TEXT;
              if (isCode) q.type = RequestType.CODE;

              // Inițializăm structurile de date goale
              if (isGrid) {
                this.gridAnswers[q.id] = new Set<number>();
                if (!q.options) q.options = [];
              } else {
                this.textAnswers[q.id] = '';
                if (isCode && !q.testCases) q.testCases = [];
              }
            });
          } catch (err) {
            console.error("--> Eroare la procesarea întrebărilor:", err);
          }

          // Pornim cronometrul pe backend (Start Quiz Attempt)
          if (this.studentId) {
            this.quizService.startQuiz(this.quizId, this.studentId).subscribe({
                error: (e) => console.warn("Nu s-a putut marca startul testului pe server", e)
            });
          }
        },
        error: (err) => {
          console.error("--> API Error:", err);
          this.errorMessage = "Eroare la încărcarea testului. Verifică consola.";
        }
      });
  }
  
  // Gestionează bifarea/debifarea opțiunilor la grile
  toggleOption(questionId: number, optionId: number, event: any) {
    const checked = event.target.checked;
    if (checked) {
      this.gridAnswers[questionId].add(optionId);
    } else {
      this.gridAnswers[questionId].delete(optionId);
    }
  }

  // Compilează și rulează codul Python
  runCode(questionId: number) {
     const code = this.textAnswers[questionId];
     const question = this.quiz?.questions.find((item) => item.id === questionId);
     const testCases = question?.testCases ?? [];

     if (!code) return;
     
     this.isRunning[questionId] = true;
     this.consoleOutputs[questionId] = "Compiling & Running...\n";

     // Execută testele unul câte unul
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
             this.consoleOutputs[questionId] = "Eroare de conexiune la compilator.";
             this.cdr.detectChanges();
        }
     });
  }

  // Trimite testul final
  submitQuiz() {
    if (!confirm("Ești sigur că vrei să trimiți testul?")) return;
    
    const responses = [];
    
    // Colectăm răspunsurile GRID
    for (const qId in this.gridAnswers) {
      responses.push({
        questionId: Number(qId),
        selectedOptionIds: Array.from(this.gridAnswers[qId]),
        textAnswer: ''
      });
    }
    
    // Colectăm răspunsurile TEXT și CODE
    for (const qId in this.textAnswers) {
      responses.push({
        questionId: Number(qId),
        selectedOptionIds: [],
        textAnswer: this.textAnswers[qId]
      });
    }

    // Trimitem la Backend
    this.quizService.submitQuiz(this.quizId, responses, this.studentId).subscribe({
      next: () => {
        alert("Test trimis cu succes!");
        this.router.navigate(['/student']);
      },
      error: (err) => {
        console.error(err);
        alert("Eroare la trimiterea testului.");
      }
    });
  }
}