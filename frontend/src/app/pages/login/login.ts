import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(4)]]
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      alert("Te rog să introduci un email si o parola valida");
      alert("Verificati ca email-ul sa fie formatat corect si parola sa aiba minim 4 caractere!")
      return;
    }
    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        alert("Autentificare reusita!");
        localStorage.setItem('currentUser', JSON.stringify(response));
        if (response.role === 'TEACHER') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/student']);
        }
      },
      error: (err) => {
        alert("Email sau parola gresita!");
      }
});
  }
}
