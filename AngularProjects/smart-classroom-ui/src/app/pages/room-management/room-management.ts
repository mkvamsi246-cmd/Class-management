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

  editingRoomId: number | null = null;
  roomNumber = '';
  roomType = '';
  status = 'AVAILABLE';

  selectedFile: File | null = null;
  isUploading = false;

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

  editRoom(room: any) {
    this.editingRoomId = room.id;
    this.roomNumber = room.roomNumber || '';
    this.roomType = room.roomType || '';
    this.status = room.status || 'AVAILABLE';

    const formCard = document.querySelector('.form-card:not(.excel-card)');
    if (formCard) {
      formCard.scrollIntoView({ behavior: 'smooth' });
    }
  }

  cancelEdit() {
    this.editingRoomId = null;
    this.roomNumber = '';
    this.roomType = '';
    this.status = 'AVAILABLE';
  }

  saveRoom() {
    if (!this.roomNumber.trim() || !this.roomType) {
      alert('Please fill in Room Number and Room Type.');
      return;
    }

    const room = {
      roomNumber: this.roomNumber.trim(),
      roomType: this.roomType,
      status: this.status
    };

    if (this.editingRoomId) {
      this.roomService
        .updateRoom(this.editingRoomId, room)
        .subscribe({
          next: () => {
            alert('Room Updated Successfully');
            this.cancelEdit();
            this.loadRooms();
          },
          error: (err) => {
            alert('Failed to update room: ' + (err?.error || err?.message));
          }
        });
    } else {
      this.roomService
        .addRoom(room)
        .subscribe({
          next: () => {
            alert('Room Added Successfully');
            this.cancelEdit();
            this.loadRooms();
          },
          error: (err) => {
            alert('Failed to add room: ' + (err?.error || err?.message));
          }
        });
    }
  }

  deleteRoom(id: number) {
    if (!confirm('Delete this room?')) return;
    this.roomService.deleteRoom(id).subscribe(() => {
      if (this.editingRoomId === id) {
        this.cancelEdit();
      }
      this.loadRooms();
    });
  }

  onFileSelected(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];
    }
  }

  uploadExcel() {
    if (!this.selectedFile) {
      alert('Please select an Excel file (.xlsx) to upload.');
      return;
    }

    this.isUploading = true;
    this.roomService.uploadRoomsExcel(this.selectedFile).subscribe({
      next: (res: any) => {
        alert(res || 'Rooms imported successfully!');
        this.selectedFile = null;
        this.isUploading = false;
        this.loadRooms();
      },
      error: (err: any) => {
        this.isUploading = false;
        const msg = err?.error || err?.message || 'Error uploading file';
        alert('Upload failed: ' + msg);
      }
    });
  }
}