import { Component, OnInit, Inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ClassService } from '../../services/class';
import { QuizService } from '../../services/quizz';
import { ClassroomDashboard } from '../../interfaces/dashboard';

@Component({
  selector: 'app-class-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './class-dashboard.html',
  styleUrl: './class-dashboard.css',
})
export class ClassDashboard {
classId: number = 0;
  teacherId: number = 0;
  dashboardData: ClassroomDashboard | null = null;
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private classService: ClassService,
    private quizService: QuizService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      const userString = localStorage.getItem('currentUser');
      if (userString) {
        const user = JSON.parse(userString);
        this.teacherId = user.id;
      }

      this.route.paramMap.subscribe(params => {
        const id = params.get('id');
        if (id) {
          this.classId = Number(id);
          this.loadDashboard();
        }
      });
    }
  }

  loadDashboard() {
    this.isLoading = true;
    this.classService.getClassroomDashboard(this.classId, this.teacherId).subscribe({
      next: (data) => {
        this.dashboardData = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        alert("Eroare la încărcarea datelor clasei.");
        this.router.navigate(['/admin']);
      }
    });
  }


  createQuiz() {
    alert("Urmează funcționalitatea de creare quiz!");
  }

  deleteQuiz(quizId: number) {
    if(confirm("Sigur vrei să ștergi acest test? Acțiunea este ireversibilă.")) {
      this.classService.deleteQuizz(quizId).subscribe(() => {
        alert("Quiz-ul selectat a fost sters!")
        this.loadDashboard();
      });
    }
  }

  editQuiz(quiz: any) {
    const newTitle = prompt("Modifică Titlul:", quiz.title);
    if (newTitle === null) return;

    const newTimeLimitStr = prompt("Modifică Durata (minute):", quiz.timeLimit);
    if (newTimeLimitStr === null) return;
    const newTimeLimit = parseInt(newTimeLimitStr);

    const currentStart = quiz.activeFrom ? quiz.activeFrom.replace(' ', 'T') : '';
    const newActiveFrom = prompt("Data Început (Format: YYYY-MM-DDTHH:mm:ss):", currentStart);
    if (newActiveFrom === null) return;

    const currentEnd = quiz.activeUntil ? quiz.activeUntil.replace(' ', 'T') : '';
    const newActiveUntil = prompt("Data Sfârșit (Format: YYYY-MM-DDTHH:mm:ss):", currentEnd);
    if (newActiveUntil === null) return;

    const updateRequest = {
      title: newTitle,
      timeLimit: newTimeLimit,
      activeFrom: newActiveFrom,
      activeUntil: newActiveUntil,
      classroomId: this.classId
    };

    this.quizService.updateQuiz(quiz.quizId, updateRequest).subscribe({
      next: (response) => {
        alert("Quiz actualizat cu succes!");
        this.loadDashboard(); 
      },
      error: (err) => {
        console.error(err);
        alert("Eroare la actualizare: " + (err.error || err.message));
      }
    });
  }

  copyCode() {
    if (this.dashboardData?.code) {
      navigator.clipboard.writeText(this.dashboardData.code);
      alert("Codul a fost copiat!");
    }
  }

  deleteClass() {
    const confirmation = prompt("Pentru a șterge clasa, scrie 'STERGE' în căsuța de mai jos:");
    if (confirmation === 'STERGE') {
      this.classService.deleteClassroom(this.classId).subscribe({
        next: () => {
          alert("Clasa a fost ștearsă.");
          this.router.navigate(['/admin']);
        },
        error: (err) => alert("Eroare la ștergere.")
      });
    }
  }
  navigate(){
    this.router.navigate(['/admin']);
  }
  hasQuizStarted(startDate: string): boolean {
    return new Date(startDate) < new Date();
  }
}
