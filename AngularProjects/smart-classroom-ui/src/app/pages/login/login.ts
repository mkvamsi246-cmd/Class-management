import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  email = '';
  password = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login() {

    const loginData = {

      email: this.email,
      password: this.password

    };

    this.authService.login(loginData)
      .subscribe({

        next: (response: any) => {

          localStorage.setItem(
            'user',
            JSON.stringify(response)
          );

          localStorage.setItem(
            'role',
            response.role
          );

          if (response.role === 'ADMIN') {

            this.router.navigate([
              '/admin-dashboard'
            ]);

          } else if (response.role === 'FACULTY') {

            this.router.navigate([
              '/faculty-dashboard'
            ]);

          }

        },

        error: (error) => {

          alert('Invalid Email Or Password');

          console.log(error);

        }

      });

  }

}