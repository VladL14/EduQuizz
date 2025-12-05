import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ClassService {
  private apiUrl = 'http://localhost:8080/api/classEnrollments';

  constructor(private http: HttpClient) { }
  getStudentClasses(studentId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/classrooms/${studentId}`);
  }
  enrollStudent(studentId: number, code: string): Observable<any> {
    const body = {
      studentId: studentId,
      code: code
    };
    return this.http.put(`${this.apiUrl}/enrollStudent`, body);
  }
}