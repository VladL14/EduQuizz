import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterLink,ReactiveFormsModule, CommonModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  registerForm: FormGroup;

  constructor(private fb: FormBuilder, private authService:AuthService, private router: Router){
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(4)]],
      confirmPassword: ['', Validators.required]
    });
  }
  onSubmit() {
    if (this.registerForm.valid) {
      if (this.registerForm.value.password !== this.registerForm.value.confirmPassword) {
        alert('Parolele nu coincid');
        return;
      }
      this.authService.register(this.registerForm.value).subscribe({
        next: (response) => {
            alert("Contul dvs. a fost creat cu succes!");
            this.router.navigate(['/login'])
        }
      })
    } else {
      alert('formular invalid');
      alert("Verificati ca username-ul sa aiba minim 3 caractere si parola minim 4 caractere")
      this.registerForm.markAllAsTouched();
    }
  }
}
