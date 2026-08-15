import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { RoomService } from '../../services/room';

@Component({
  selector: 'app-room-management',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './room-management.html',
  styleUrl: './room-management.css'
})
export class RoomManagement implements OnInit {

  roomList: any[] = [];
  role = '';

  // Only two fields needed
  roomNumber = '';
  roomType = '';

  constructor(private roomService: RoomService) {}

  ngOnInit(): void {
    this.role = localStorage.getItem('role') || '';
    this.loadRooms();
  }

  loadRooms() {
    this.roomService
      .getAllRooms()
      .subscribe((response: any) => {
        this.roomList = response;
      });
  }

  addRoom() {
    if (!this.roomNumber.trim() || !this.roomType) {
      alert('Please fill in Room Number and Room Type.');
      return;
    }

    const room = {
      roomNumber: this.roomNumber.trim(),
      roomType: this.roomType,
      status: 'AVAILABLE'
    };

    this.roomService
      .addRoom(room)
      .subscribe(() => {
        alert('Room Added Successfully');
        this.roomNumber = '';
        this.roomType = '';
        this.loadRooms();
      });
  }

  deleteRoom(id: number) {
    if (!confirm('Delete this room?')) return;
    this.roomService.deleteRoom(id).subscribe(() => {
      this.loadRooms();
    });
  }
}