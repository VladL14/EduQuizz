import { Component, OnInit, ChangeDetectorRef, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { QuizService } from '../../services/quizz';
import { Quiz } from '../../interfaces/quizz';
import { QuizAttempt } from '../../interfaces/quizz-atempt';

interface QuizWithGrade extends Quiz{
  myGrade?: number | null;
}

@Component({
  selector: 'app-student-class-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './student-quizz.html',
})
export class StudentQuizz implements OnInit {
  classId: number = 0;
  studentId: number = 0;
  
  activeQuizzes: QuizWithGrade[] = [];
  upcomingQuizzes: QuizWithGrade[] = [];
  pastQuizzes: QuizWithGrade[] = [];

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
    this.quizService.getQuizzesByClassroom(this.classId).subscribe({
      next: (data) => {
        this.categorizeQuizzes(data);
        if(this.studentId){
          this.loadGrades();
        }
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  loadGrades() {
      this.quizService.getStudentAttempts(this.classId, this.studentId).subscribe(attempts => {
          this.pastQuizzes.forEach(quiz => {
              const attempt = attempts.find((a: any) => a.quiz.id === quiz.id);
              if (attempt) {
                  quiz.myGrade = attempt.grade;
              }
          });
          this.cdr.detectChanges();
      });
  }

  categorizeQuizzes(quizzes: Quiz[]) {
    const now = new Date(); 

    this.activeQuizzes = [];
    this.upcomingQuizzes = [];
    this.pastQuizzes = [];

    quizzes.forEach(quiz => {
      const fromDate = new Date(quiz.activeFrom);
      const untilDate = new Date(quiz.activeUntil);

      if (now < fromDate) {
        this.upcomingQuizzes.push(quiz);
      } 
      else if (now >= fromDate && now <= untilDate) {
        this.activeQuizzes.push(quiz);
      } 
      else {
        this.pastQuizzes.push(quiz);
      }
    });
  }

  goBack() {
    this.router.navigate(['/student']);
  }

  startQuiz(quizId: number) {
    this.router.navigate(['/student/quiz', quizId, 'take']);
  }
}