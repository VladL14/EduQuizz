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
    private quizService: QuizService, // <--- Avem nevoie de asta pentru stergere
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
        console.log("Dashboard Data:", data); // Debugging
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
    // CORECTURA 1: Folosim 'id' si 'title' conform noului DTO
    this.editFormData = {
      quizId: quiz.id,       // <--- AICI ERA quiz.quizId
      title: quiz.title,     // <--- AICI ERA quiz.name
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
    // Ajustare simplă pentru input datetime-local
    const offset = date.getTimezoneOffset();
    const localDate = new Date(date.getTime() - offset * 60000);
    return localDate.toISOString().slice(0, 16);
  }

  createQuiz() {
    this.router.navigate(['/teacher/create-quiz'], { queryParams: { classId: this.classId } });
  }

  deleteQuiz(quizId: number) {
    if(confirm("Sigur vrei să stergi acest test? Actiunea este ireversibila.")) {
      // CORECTURA 2: Apelam quizService, nu classService
      // Metoda deleteQuiz este in QuizService (si in Controller-ul de Quizzes)
      this.quizService.deleteQuiz(quizId).subscribe({
        next: () => {
             alert("Quiz-ul selectat a fost sters!");
             this.loadDashboard();
        },
        error: (err) => alert("Eroare la ștergerea testului.")
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

  gradeStudent(studentId: number, quizId: number)
  {
    this.quizService.getAttemptByStudentAndQuiz(studentId, quizId).subscribe({
      next: (attempt) => {
        if(attempt && attempt.id)
        {
          this.router.navigate(['/teacher/grade', attempt.id]);
        }
        else
        {
          alert("Nu există nicio tentativă pentru acest student și test.");
        }
  },
      error: () => alert("Eroare la preluarea tentativei studentului.")
      });
    }

  getGradeClass(grade: any): string {
    // 1. Verificăm dacă nota există
    if (grade === null || grade === undefined || grade === '') {
      return 'bg-gray-100 text-gray-400 border-gray-200'; // Gri (lipsă)
    }

    const strGrade = String(grade).trim(); // Convertim sigur la string

    // 2. CAZ GALBEN: "IP" sau începe cu "P:"
    if (strGrade === 'IP' || strGrade.startsWith('P:')) {
      return 'bg-yellow-100 text-yellow-700 border-yellow-200';
    }

    const numericGrade = parseFloat(strGrade);

    // 3. CAZ VERDE: Notă >= 5
    if (!isNaN(numericGrade) && numericGrade >= 5) {
      return 'bg-green-100 text-green-700 border-green-200';
    }

    // 4. CAZ ROȘU: Notă < 5
    return 'bg-red-100 text-red-700 border-red-200';
  }

  getGradeText(grade: any): string {
    if (grade === null || grade === undefined || grade === '') return '-';

    const strGrade = String(grade).trim();

    if (strGrade === 'IP') {
      return 'În Curs';
    }

    // Aici eliminăm "P:" și afișăm doar nota + (Review)
    if (strGrade.startsWith('P:')) {
      return strGrade.substring(2) + ' (Review)';
    }

    return strGrade;
  }
  }
