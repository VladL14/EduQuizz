import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CompilerRequest, CompilerResponse, Quiz, SubmitQuizRequest } from '../interfaces/quizz';
import { QuizAttempt } from '../interfaces/quizz-atempt';
import { QuestionRequest } from '../interfaces/question';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private apiUrl = 'http://localhost:8080/api/quizzes'; 
  private attemptUrl = 'http://localhost:8080/api/quizAttempt';
  private questionUrl = 'http://localhost:8080/api/questions';
  private compilerUrl = 'http://localhost:8080/api/compiler';

  constructor(private http: HttpClient) {}
  getQuizzesByClassroom(classroomId: number): Observable<Quiz[]> {
    return this.http.get<Quiz[]>(`${this.apiUrl}/classroom/${classroomId}`);
  }

  updateQuiz(quizId: number, quizData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/update/${quizId}`, quizData);
  }
  getStudentAttempts(classroomId: number, studentId: number): Observable<QuizAttempt[]> {
    return this.http.get<QuizAttempt[]>(
      `${this.attemptUrl}/student/classroom/${classroomId}?studentId=${studentId}`
    );
  }
  createQuiz(quizData: any): Observable<Quiz> {
    return this.http.post<Quiz>(`${this.apiUrl}/create`, quizData);
  }
  createQuestion(quizId: number, questionData: QuestionRequest): Observable<any> {
    return this.http.post(`${this.questionUrl}/create/${quizId}`, questionData);
  }

  getQuizById(id: number): Observable<Quiz> {
    return this.http.get<Quiz>(`${this.apiUrl}/${id}`);
  }

  startQuiz(quizId: number, studentId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${quizId}/start`, null, {
      params: { studentId }
    });
  }


  runCode(data: CompilerRequest): Observable<CompilerResponse> {
    return this.http.post<CompilerResponse>(`${this.compilerUrl}/run`, data);
  }

  submitQuiz(quizId: number, responses: any[], studentId: number) {
    // Construim URL-ul exact cum îl vrea Backend-ul Java:
    // POST /api/quizzes/{quizId}/submit?studentId={studentId}
    return this.http.post(`${this.apiUrl}/${quizId}/submit?studentId=${studentId}`, responses);
  }
}
