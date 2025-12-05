import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './student-dashboard.html',
  styleUrl: './student-dashboard.css'
})
export class StudentDashboard {
  username: string = "Alexandru"; 
  classes = [
    {
      id: 1,
      name: 'Structuri de Date',
      teacher: 'Prof. Popescu Ion',
      description: 'Arbori, Grafuri, Liste înlănțuite și algoritmi fundamentali.',
      color: 'bg-blue-600'
    },
    {
      id: 2,
      name: 'Programare Orientată Obiect',
      teacher: 'Conf. Radu Maria',
      description: 'Concepte de bază Java, moștenire, polimorfism.',
      color: 'bg-indigo-600'
    },
    {
      id: 3,
      name: 'Baze de Date',
      teacher: 'Lect. Ionescu Dan',
      description: 'SQL, Normalizare și proiectarea bazelor de date relaționale.',
      color: 'bg-purple-600'
    },
    {
      id: 4,
      name: 'Algoritmi Genetici',
      teacher: 'Prof. Vasile Ana',
      description: 'Optimizare inspirată din biologie și evoluție.',
      color: 'bg-emerald-600'
    }
  ];

  constructor(private router: Router) {}

  logout() {
    // Ștergem token-ul (când vom avea) și redirectăm
    console.log("Logging out...");
    this.router.navigate(['/login']);
  }

  enterClass(classId: number) {
    console.log("Navigating to class ID:", classId);
    // Aici vom naviga către pagina clasei: this.router.navigate(['/class', classId]);
  }
}
