import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { RequestService } from '../../services/request';
import { RoomService } from '../../services/room';

@Component({
  selector: 'app-room-request',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './room-request.html',
  styleUrl: './room-request.css'
})
export class RoomRequest implements OnInit {

  role = '';
  facultyId = 0;

  // Admin lists
  requestList: any[] = [];
  adminTab: 'UNOCCUPIED' | 'SWAP' = 'UNOCCUPIED';

  // Faculty UNOCCUPIED form
  availableRooms: any[] = [];
  selectedRoomId: number | null = null;
  requestDate = '';
  startTime = '';
  endTime = '';
  reason = '';

  constructor(
    private requestService: RequestService,
    private roomService: RoomService
  ) {}

  ngOnInit(): void {
    this.role = localStorage.getItem('role') || '';
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.facultyId = user.facultyId || user.id || 0;

    if (this.role === 'ADMIN') {
      this.loadRequests();
    } else {
      this.loadAvailableRooms();
    }
  }

  loadRequests() {
    this.requestService.getRequests().subscribe((res: any) => {
      this.requestList = res;
    });
  }

  loadAvailableRooms() {
    this.roomService.getAvailableRooms().subscribe((res: any) => {
      this.availableRooms = res;
    });
  }

  get unoccupiedRequests() {
    return this.requestList.filter(r => r.requestType === 'UNOCCUPIED' || !r.requestType);
  }

  get swapRequests() {
    return this.requestList.filter(r => r.requestType === 'SWAP');
  }

  selectRoom(roomId: number) {
    this.selectedRoomId = this.selectedRoomId === roomId ? null : roomId;
  }

  submitUnoccupied() {
    if (!this.selectedRoomId || !this.requestDate || !this.startTime || !this.endTime) {
      alert('Please select a room, date, and time slot.');
      return;
    }

    this.requestService
      .createUnoccupiedRequest(
        this.facultyId,
        this.selectedRoomId,
        this.requestDate,
        this.startTime,
        this.endTime,
        this.reason
      )
      .subscribe({
        next: () => {
          alert('Request submitted successfully!');
          this.selectedRoomId = null;
          this.requestDate = '';
          this.startTime = '';
          this.endTime = '';
          this.reason = '';
        },
        error: (err) => {
          alert('Error: ' + (err.error || err.message));
        }
      });
  }

  approve(id: number) {
    this.requestService.approveRequest(id).subscribe(() => this.loadRequests());
  }

  reject(id: number) {
    this.requestService.rejectRequest(id).subscribe(() => this.loadRequests());
  }
}