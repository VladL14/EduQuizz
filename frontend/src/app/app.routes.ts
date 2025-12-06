import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { AdminDashboard } from './pages/admin-dashboard/admin-dashboard';
import { StudentDashboard } from './pages/student-dashboard/student-dashboard';
import { StudentQuizz } from './pages/student-quizz/student-quizz';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'admin', component: AdminDashboard },
  { path: 'student', component: StudentDashboard },
  { path: 'student/class/:id', component: StudentQuizz},
  { path: '**', redirectTo: 'login' }
];