import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.css'
})
export class Notifications implements OnInit {

  notificationList: any[] = [];
  loading = true;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userId = user.id || 0;

    this.http
      .get<any[]>(`http://localhost:8080/api/notifications/${userId}`)
      .subscribe({
        next: (data) => {
          // Show newest first
          this.notificationList = data.reverse();
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
  }
}
