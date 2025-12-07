import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Classroom } from '../interfaces/classroom'

@Injectable({
  providedIn: 'root'
})
export class ClassService {
  private enrollmentUrl = 'http://localhost:8080/api/classEnrollments';
  private classroomUrl = 'http://localhost:8080/api/classrooms';

  constructor(private http: HttpClient) { }
  getStudentClasses(studentId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.enrollmentUrl}/classrooms/${studentId}`);
  }
  enrollStudent(studentId: number, code: string): Observable<any> {
    const body = {
      studentId: studentId,
      code: code
    };
    return this.http.post(`${this.enrollmentUrl}/enrollStudent`, body);
  }
  createClassroom(teacherId: number, className: string): Observable<Classroom> {
    const requestBody = { teacherId, className };
    return this.http.post<Classroom>(`${this.classroomUrl}/create`, requestBody);
  }

  getTeacherClasses(teacherId: number): Observable<Classroom[]> {
    return this.http.get<Classroom[]>(`${this.classroomUrl}/teacher/${teacherId}`);
}
}