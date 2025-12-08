import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Quiz } from '../interfaces/quizz';
import { QuizAttempt } from '../interfaces/quizz-atempt';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private apiUrl = 'http://localhost:8080/api/quizzes'; 
  private attemptUrl = 'http://localhost:8080/api/quizAttempt'

  constructor(private http: HttpClient) {}
  getQuizzesByClassroom(classroomId: number): Observable<Quiz[]> {
    return this.http.get<Quiz[]>(`${this.apiUrl}/${classroomId}`);
  }

  updateQuiz(quizId: number, quizData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/update/${quizId}`, quizData);
  }
  getStudentAttempts(classroomId: number, studentId: number): Observable<QuizAttempt[]> {
    return this.http.get<QuizAttempt[]>(
      `${this.attemptUrl}/student/classroom/${classroomId}?studentId=${studentId}`
    );
  }
}