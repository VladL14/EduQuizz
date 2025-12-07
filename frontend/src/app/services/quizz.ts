import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Quiz } from '../interfaces/quizz';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private apiUrl = 'http://localhost:8080/api/quizzes'; 

  constructor(private http: HttpClient) {}
  getQuizzesByClassroom(classroomId: number): Observable<Quiz[]> {
    return this.http.get<Quiz[]>(`${this.apiUrl}/${classroomId}`);
  }
}