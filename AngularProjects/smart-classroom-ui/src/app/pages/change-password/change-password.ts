import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './change-password.html',
  styleUrl: './change-password.css'
})
export class ChangePassword {

  oldPassword = '';
  newPassword = '';

  constructor(
    private authService: AuthService
  ) {}

  save() {

  const user =
    JSON.parse(localStorage.getItem('user') || '{}');

  const data = {

    userId: user.id,
    oldPassword: this.oldPassword,
    newPassword: this.newPassword,

  };

  this.authService
    .changePassword(data)
    .subscribe({

      next: (response: any) => {

        alert('Password Changed Successfully');

      },

      error: (error) => {

        console.log(error);

      }

    });

}

}