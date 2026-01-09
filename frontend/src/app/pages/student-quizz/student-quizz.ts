  import { Component, OnInit, ChangeDetectorRef, Inject, PLATFORM_ID } from '@angular/core';
  import { CommonModule, isPlatformBrowser } from '@angular/common';;
  import { QuizService } from '../../services/quizz';
  import { ActivatedRoute, Router, RouterModule } from '@angular/router';

  export interface QuizSummaryDTO {
    id: number;
    title: string;
    activeFrom: string;
    activeUntil: string;
    timeLimit: number;
    // Statusurile posibile calculate de Backend
    status: 'NEW' | 'IN_PROGRESS' | 'COMPLETED' | 'PENDING_REVIEW' | 'EXPIRED';
    grade?: number | null;
  }

  @Component({
    selector: 'app-student-class-detail',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './student-quizz.html',
  })
  export class StudentQuizz implements OnInit {
    classId: number = 0;
    studentId: number = 0;

    // Folosim noua interfață
    activeQuizzes: QuizSummaryDTO[] = [];
    upcomingQuizzes: QuizSummaryDTO[] = [];
    pastQuizzes: QuizSummaryDTO[] = [];

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
          if (userString) {
              this.studentId = JSON.parse(userString).id;
          }
      }

      this.route.paramMap.subscribe(params => {
        const idParam = params.get('id');
        if (idParam) {
          this.classId = Number(idParam);
          this.loadQuizzes();
        }
      });
    }

    loadQuizzes() {
      this.quizService.getQuizSummariesByClassroom(this.classId, this.studentId).subscribe({
        next: (data: QuizSummaryDTO[]) => {
          this.categorizeQuizzes(data);
          this.cdr.detectChanges();
        },
        error: (err) => console.error("Eroare la încărcarea dashboard-ului:", err)
      });
    }

    categorizeQuizzes(quizzes: QuizSummaryDTO[]) {
      const now = new Date();

      this.activeQuizzes = [];
      this.upcomingQuizzes = [];
      this.pastQuizzes = [];

      quizzes.forEach(quiz => {
        const fromDate = new Date(quiz.activeFrom);

        // 1. VIITOARE: Dacă data de start e în viitor
        if (fromDate > now) {
          this.upcomingQuizzes.push(quiz);
          return;
        }

        // 2. ISTORIC: Dacă e finalizat, expirat sau în așteptare de notare
        if (quiz.status === 'COMPLETED' || quiz.status === 'EXPIRED' || quiz.status === 'PENDING_REVIEW') {
          this.pastQuizzes.push(quiz);
          return;
        }

        // 3. ACTIVE: Dacă e NEW sau IN_PROGRESS și perioada e validă
        if (quiz.status === 'NEW' || quiz.status === 'IN_PROGRESS') {
          this.activeQuizzes.push(quiz);
        }
      });
    }

    goBack() {
      this.router.navigate(['/student']);
    }

    startQuiz(quizId: number) {
      // Această rută trebuie să ducă la pagina de "Take Quiz" pe care am reparat-o anterior
      this.router.navigate(['/student/quiz', quizId, 'take']);
    }
  }
