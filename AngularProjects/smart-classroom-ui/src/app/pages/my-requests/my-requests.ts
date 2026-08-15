import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RequestService } from '../../services/request';

@Component({
  selector: 'app-my-requests',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-requests.html',
  styleUrl: './my-requests.css'
})
export class MyRequests implements OnInit {

  requestList: any[] = [];

  constructor(private requestService: RequestService) {}

  ngOnInit(): void {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const facultyId = user.facultyId || user.id || 0;

    this.requestService
      .getFacultyRequests(facultyId)
      .subscribe((response: any) => {
        this.requestList = response;
      });
  }
}