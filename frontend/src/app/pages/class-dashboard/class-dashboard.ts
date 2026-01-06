import { Component, OnInit, Inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ClassService } from '../../services/class';
import { QuizService } from '../../services/quizz';
import { ClassroomDashboard, QuizSummary } from '../../interfaces/dashboard';

@Component({
  selector: 'app-class-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './class-dashboard.html',
  styleUrl: './class-dashboard.css',
})
export class ClassDashboard {
classId: number = 0;
  teacherId: number = 0;
  dashboardData: ClassroomDashboard | null = null;
  isLoading: boolean = true;
  isEditModalOpen: boolean = false;
  isSaving: boolean = false;

  editFormData = {
    quizId: 0,
    title: '',
    timeLimit: 0,
    activeFrom: '',
    activeUntil: ''
  };

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

  openEditModal(quiz: QuizSummary) {
    this.editFormData = {
      quizId: quiz.quizId,
      title: quiz.name,
      timeLimit: quiz.timeLimit,
      activeFrom: this.formatDateForInput(quiz.activeFrom),
      activeUntil: this.formatDateForInput(quiz.activeUntil)
    };
    
    this.isEditModalOpen = true;
  }

  closeEditModal() {
    this.isEditModalOpen = false;
    this.isSaving = false;
  }

  saveQuizChanges() {
    if (!this.editFormData.title || !this.editFormData.activeFrom || !this.editFormData.activeUntil) {
        alert("Toate campurile sunt obligatorii!");
        return;
    }

    this.isSaving = true;

    const updateRequest = {
      title: this.editFormData.title,
      timeLimit: this.editFormData.timeLimit,
      activeFrom: this.editFormData.activeFrom, 
      activeUntil: this.editFormData.activeUntil,
      classroomId: this.classId
    };

    this.quizService.updateQuiz(this.editFormData.quizId, updateRequest).subscribe({
      next: () => {
        this.closeEditModal();
        this.loadDashboard();
      },
      error: (err) => {
        console.error(err);
        alert("Eroare la actualizare: " + (err.error || err.message));
        this.isSaving = false;
      }
    });
  }
  private formatDateForInput(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    const offset = date.getTimezoneOffset();
    const localDate = new Date(date.getTime() - offset * 60000);
    return localDate.toISOString().slice(0, 16);
  }

  createQuiz() {
    this.router.navigate(['/teacher/create-quiz'], { queryParams: { classId: this.classId } });
  }

  deleteQuiz(quizId: number) {
    if(confirm("Sigur vrei să stergi acest test? Actiunea este ireversibila.")) {
      this.classService.deleteQuizz(quizId).subscribe(() => {
        alert("Quiz-ul selectat a fost sters!")
        this.loadDashboard();
      });
    }
  }

  copyCode() {
    if (this.dashboardData?.code) {
      navigator.clipboard.writeText(this.dashboardData.code);
      alert("Codul a fost copiat!");
    }
  }

  deleteClass() {
    const confirmation = prompt("Pentru a sterge clasa, scrie 'STERGE' în casuta de mai jos:");
    if (confirmation === 'STERGE') {
      this.classService.deleteClassroom(this.classId).subscribe({
        next: () => {
          alert("Clasa a fost stearsă.");
          this.router.navigate(['/admin']);
        },
        error: (err) => alert("Eroare la stergere.")
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
