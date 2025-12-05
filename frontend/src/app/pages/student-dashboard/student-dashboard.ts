import { Component, OnInit,Inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { CommonModule,isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { ClassService } from '../../services/class';

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './student-dashboard.html',
  styleUrl: './student-dashboard.css'
})
export class StudentDashboard implements OnInit {
  username: string = "";
  userId: number = 0;
  classes: any[] = [];

  colors = ['bg-blue-600', 'bg-indigo-600', 'bg-purple-600', 'bg-emerald-600', 'bg-red-600', 'bg-orange-600'];

  constructor(private router: Router, private classService: ClassService, @Inject(PLATFORM_ID) private platformId: Object, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
    const userString = localStorage.getItem('currentUser');
    
    if (userString) {
      const user = JSON.parse(userString);
      this.username = user.username;
      this.userId = user.id;

      this.loadClasses();
    } else {
      this.router.navigate(['/login']);
    }
  }
}

  loadClasses() {
    this.classService.getStudentClasses(this.userId).subscribe({
      next: (data) => {
        console.log(data)
        this.classes = data.map((cls, index) => ({
          ...cls,
          color: this.colors[index % this.colors.length]
        }));
        console.log(this.classes)
        this.cdr.detectChanges()
      },
      error: (err) => {
        alert("Nu sunt clase disponibile!");
      }
    });
  }

  enroll() {
    const code = prompt("Introdu codul unic al clasei (primit de la profesor):");

    if (code) {
      this.classService.enrollStudent(this.userId, code).subscribe({
        next: () => {
          alert("Te-ai inscris cu succes!");
          this.loadClasses();
        },
        error: () => {
          alert("Cod invalid sau deja înscris.");
        }
      });
    }
  }

  logout() {
    localStorage.removeItem('currentUser');
    this.router.navigate(['/login']);
  }

  enterClass(classId: number) {
    console.log("Intrăm în clasa:", classId);
  }
}