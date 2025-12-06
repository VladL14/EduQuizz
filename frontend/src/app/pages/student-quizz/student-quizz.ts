import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { QuizService } from '../../services/quizz';
import { Quiz } from '../../interfaces/quizz';

@Component({
  selector: 'app-student-class-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './student-quizz.html',
})
export class StudentQuizz implements OnInit {
  classId: number = 0;
  
  activeQuizzes: Quiz[] = [];
  upcomingQuizzes: Quiz[] = [];
  pastQuizzes: Quiz[] = [];

  constructor(
    private route: ActivatedRoute, 
    private router: Router,
    private quizService: QuizService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.classId = Number(idParam);
        this.loadQuizzes();
      }
    });
  }

  loadQuizzes() {
    this.quizService.getQuizzesByClassroom(this.classId).subscribe({
      next: (data) => {
        // Logica de sortare actualizată pentru activeFrom / activeUntil
        this.categorizeQuizzes(data);
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  categorizeQuizzes(quizzes: Quiz[]) {
    const now = new Date(); 

    this.activeQuizzes = [];
    this.upcomingQuizzes = [];
    this.pastQuizzes = [];

    quizzes.forEach(quiz => {
      // Convertim string-urile în Date
      const fromDate = new Date(quiz.activeFrom);
      const untilDate = new Date(quiz.activeUntil);

      if (now < fromDate) {
        // Încă nu a început -> VIITOR
        this.upcomingQuizzes.push(quiz);
      } 
      else if (now >= fromDate && now <= untilDate) {
        // Suntem între data de start și data de final -> ACTIV
        this.activeQuizzes.push(quiz);
      } 
      else {
        // A trecut de data limită -> TRECUT
        this.pastQuizzes.push(quiz);
      }
    });
  }

  goBack() {
    this.router.navigate(['/student']);
  }

  startQuiz(quizId: number) {
    // Navigarea spre pagina de test (urmează să o facem)
    this.router.navigate(['/student/quiz', quizId, 'take']);
  }
}