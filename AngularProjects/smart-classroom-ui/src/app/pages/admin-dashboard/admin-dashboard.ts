import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../services/dashboard';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css'
})
export class AdminDashboard implements OnInit {

  dashboardData: any = {};

  constructor(
    private dashboardService: DashboardService
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard() {

    this.dashboardService
      .getDashboardData()
      .subscribe({

        next: (response: any) => {

          console.log(response);

          this.dashboardData = response;

        },

        error: (error) => {

          console.log(error);

        }

      });
  }
}