import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http'; // <--- AM ADAUGAT HttpHeaders
import { Observable } from 'rxjs';
import { CompilerRequest, CompilerResponse, Quiz } from '../interfaces/quizz';
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

  // Helper privat pentru a genera header-ul cu token
  private getAuthHeaders(): HttpHeaders {
    const userString = localStorage.getItem('currentUser');
    const token = userString ? JSON.parse(userString).token : '';
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getQuizSummariesByClassroom(classroomId: number, studentId: number) {
    return this.http.get<any[]>(`${this.apiUrl}/classroom/${classroomId}/student/${studentId}`, { headers: this.getAuthHeaders() });
  }

  updateQuiz(quizId: number, quizData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/update/${quizId}`, quizData, { headers: this.getAuthHeaders() });
  }

  getStudentAttempts(classroomId: number, studentId: number): Observable<QuizAttempt[]> {
    return this.http.get<QuizAttempt[]>(
      `${this.attemptUrl}/student/classroom/${classroomId}?studentId=${studentId}`,
      { headers: this.getAuthHeaders() }
    );
  }

  createQuiz(quizData: any): Observable<Quiz> {
    return this.http.post<Quiz>(`${this.apiUrl}/create`, quizData, { headers: this.getAuthHeaders() });
  }

  createQuestion(quizId: number, questionData: QuestionRequest): Observable<any> {
    return this.http.post(`${this.questionUrl}/create/${quizId}`, questionData, { headers: this.getAuthHeaders() });
  }

  getQuizById(id: number): Observable<Quiz> {
    return this.http.get<Quiz>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }

  startQuiz(quizId: number, studentId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${quizId}/start`, null, {
      headers: this.getAuthHeaders(),
      params: { studentId }
    });
  }

  runCode(data: CompilerRequest): Observable<CompilerResponse> {
    return this.http.post<CompilerResponse>(`${this.compilerUrl}/run`, data, { headers: this.getAuthHeaders() });
  }

  submitQuiz(quizId: number, responses: any[], studentId: number) {
    return this.http.post(`${this.apiUrl}/${quizId}/submit?studentId=${studentId}`, responses, { headers: this.getAuthHeaders() });
  }

  deleteQuiz(id: number): Observable<any> {
      return this.http.delete(`${this.apiUrl}/delete/${id}`, { headers: this.getAuthHeaders(), responseType: 'text' });
  }

  // --- METODELE CRITICE PENTRU NOTARE ---

  getAttemptById(attemptId: number): Observable<any> {
    return this.http.get<any>(`${this.attemptUrl}/${attemptId}`, { headers: this.getAuthHeaders() });
  }

  updateQuestionScore(attemptId: number, questionId: number, points: number): Observable<any> {
    return this.http.put(
        `${this.attemptUrl}/${attemptId}/grade/${questionId}?points=${points}`, 
        {}, 
        { headers: this.getAuthHeaders() }
    );
  }

  getAttemptByStudentAndQuiz(studentId: number, quizId: number): Observable<any> {
    return this.http.get<any>(
        `${this.attemptUrl}/student/${studentId}/quiz/${quizId}`,
        { headers: this.getAuthHeaders() } // <--- ASTA LIPSEA INAINTE
    );
  }
}