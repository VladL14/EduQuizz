import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms'; // <--- VERIFICĂ CĂ AI ASTA!
import { QuizService } from '../../services/quizz';

@Component({
  selector: 'app-teacher-grading',
  standalone: true,
  imports: [CommonModule, FormsModule], // <--- VERIFICĂ CĂ AI ASTA!
  templateUrl: './teacher-grading.html',
})
export class TeacherGrading implements OnInit {
  attemptId: number = 0;
  attempt: any = null;
  isLoading = true;

  // Modal Vars
  isModalOpen = false;
  modalMode: 'GRADE_INPUT' | 'INFO_SUCCESS' | 'INFO_ERROR' = 'GRADE_INPUT';
  modalTitle = '';
  modalMessage = '';
  tempScore: number = 0;
  selectedResponse: any = null;
  isSaving = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quizService: QuizService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.attemptId = Number(id);
        this.loadAttempt();
      }
    });
  }

  loadAttempt() {
    this.isLoading = true;
    this.quizService.getAttemptById(this.attemptId).subscribe({
      next: (data) => {
        this.attempt = data;
        // Sortăm răspunsurile
        if (this.attempt && this.attempt.responses) {
          this.attempt.responses.sort((a: any, b: any) => {
            const idA = a.question ? a.question.id : 0;
            const idB = b.question ? b.question.id : 0;
            return idA - idB;
          });
        }
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.isLoading = false;
        this.cdr.detectChanges();
        alert("Eroare la incarcare!"); // Fallback
      }
    });
  }

  // DESCHIDERE MODAL
  openGradeModal(response: any) {
    console.log("CLICK DETECTAT PE: ", response); // <--- DEBUG 1

    if (!response) {
      console.error("Response este null!");
      return;
    }

    this.selectedResponse = response;
    this.tempScore = response.score || 0;

    this.modalMode = 'GRADE_INPUT';
    this.modalTitle = 'Modifică Nota';
    this.modalMessage = `Introdu noul punctaj (Maxim: ${response.question?.points || 0})`;

    this.isModalOpen = true; // Deschide modalul
    console.log("isModalOpen setat la true"); // <--- DEBUG 2

    this.cdr.detectChanges(); // Forțează desenarea
  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedResponse = null;
    this.isSaving = false;
    this.cdr.detectChanges();
  }

  confirmModalAction() {
    // 1. Verificăm modul modalului
    if (this.modalMode !== 'GRADE_INPUT') {
      this.closeModal();
      return;
    }

    // 2. Verificăm dacă avem un răspuns selectat
    if (!this.selectedResponse) return;

    const maxPoints = this.selectedResponse.question ? this.selectedResponse.question.points : 0;

    // 3. Validăm nota
    if (this.tempScore < 0 || this.tempScore > maxPoints) {
      this.showModal('INFO_ERROR', 'Punctaj Invalid', `Nota trebuie să fie între 0 și ${maxPoints}.`);
      return;
    }

    // 4. DEFINIM questionId (Aici era eroarea ta - lipsea linia asta)
    const questionId = this.selectedResponse.question ? this.selectedResponse.question.id : null;

    // 5. Verificăm că ID-ul există
    if (!questionId) {
      console.error("Nu am ID intrebare");
      return;
    }

    this.isSaving = true;

    // 6. Apelăm serviciul folosind questionId-ul definit mai sus
    this.quizService.updateQuestionScore(this.attemptId, questionId, this.tempScore).subscribe({
      next: (updatedAttempt) => {
        // Actualizăm datele locale
        this.attempt.grade = updatedAttempt.grade;
        this.selectedResponse.score = this.tempScore;

        this.isSaving = false; // <--- FIXUL PENTRU BUTONUL "OK"

        // Închidem modalul curent
        this.isModalOpen = false;
        this.cdr.detectChanges();

        // Deschidem modalul de succes
        setTimeout(() => {
          this.showModal('INFO_SUCCESS', 'Succes', 'Nota a fost actualizată!');
        }, 150);
      },
      error: (err) => {
        this.isSaving = false; // Resetăm și pe eroare
        this.closeModal();
        setTimeout(() => {
          this.showModal('INFO_ERROR', 'Eroare', 'Eroare backend: ' + err.message);
        }, 150);
      }
    });
  }

  showModal(mode: 'INFO_SUCCESS' | 'INFO_ERROR', title: string, message: string) {
    this.modalMode = mode;
    this.modalTitle = title;
    this.modalMessage = message;
    this.isModalOpen = true;
    this.cdr.detectChanges();
  }

  goBack() {
    window.history.back();
  }
}
