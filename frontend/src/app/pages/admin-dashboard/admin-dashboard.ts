import { Component, OnInit, Inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ClassService } from '../../services/class';
import { Classroom } from '../../interfaces/classroom';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule], 
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css'
})
export class AdminDashboard implements OnInit {
  username: string = "";
  userId: number = 0;
  classes: any[] = [];
  isModalOpen: boolean = false;
  newClassName: string = '';
  isLoading: boolean = false;
  
  colors = ['bg-blue-600', 'bg-indigo-600', 'bg-purple-600', 'bg-emerald-600', 'bg-red-600', 'bg-orange-600'];

  constructor(
    private router: Router,
    private classService: ClassService, 
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      const userString = localStorage.getItem('currentUser');
      if (userString) {
        const user = JSON.parse(userString);
        if (user.role !== 'TEACHER') {
           this.router.navigate(['/student']);
           return;
        }

        this.username = user.username;
        this.userId = user.id;
        this.loadTeacherClasses();
      } else {
        this.router.navigate(['/login']);
      }
    }
  }

  loadTeacherClasses() {
    this.classService.getTeacherClasses(this.userId).subscribe({
      next: (data) => {
        if (data && data.length > 0) {
            this.classes = data.map((cls, index) => ({
            ...cls,
            color: this.colors[index % this.colors.length]
            }));
        } else {
            this.classes = [];
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        alert("Eroare la incarcarea claselor")
        
      }
    });
  }

  openCreateModal() {
    this.isModalOpen = true;
    this.newClassName = '';
    this.isLoading = false;
  }

  closeModal() {
    this.isModalOpen = false;
    this.isLoading = false;
  }

  createClass() {
    if (!this.newClassName.trim()) {
        alert("Te rog introdu un nume pentru clasa!");
        return;
    }

    this.isLoading = true;
    this.classService.createClassroom(this.userId, this.newClassName).subscribe({
        next: (createdClass) => {
            this.isLoading = false;
            const classWithColor = {
                ...createdClass,
                color: this.colors[this.classes.length % this.colors.length]
            };
            this.classes = [classWithColor, ...this.classes];
            this.closeModal();
            this.cdr.detectChanges();
        },
        error: (err) => {
            alert("Eroare la crearea clasei: " + (err.message || 'Eroare necunoscută'));
            this.isLoading = false;
            this.closeModal();
        },
    });
}

  manageClass(classId: number) {
    this.router.navigate(['/teacher/class', classId]);
  }

  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('currentUser');
      this.router.navigate(['/login']);
    }
  }
}