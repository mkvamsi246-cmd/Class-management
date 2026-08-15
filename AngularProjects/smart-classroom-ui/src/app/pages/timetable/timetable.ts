import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TimetableService } from '../../services/timetable';
import { FacultyService } from '../../services/faculty';
import { RoomService } from '../../services/room';

@Component({
  selector: 'app-timetable',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './timetable.html',
  styleUrl: './timetable.css'
})
export class Timetable implements OnInit {

  timetableList: any[] = [];
  facultyList: any[] = [];
  roomList: any[] = [];

  editingTimetableId: number | null = null;
  subjectName = '';
  facultyId: number | null = null;
  roomId: number | null = null;
  dayOfWeek = '';
  startTime = '';
  endTime = '';

  days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];

  selectedFile: File | null = null;
  isUploading = false;

  constructor(
    private timetableService: TimetableService,
    private facultyService: FacultyService,
    private roomService: RoomService
  ) {}

  ngOnInit(): void {
    this.loadTimetable();
    this.loadFaculty();
    this.loadRooms();
  }

  loadTimetable() {
    this.timetableService
      .getAllTimetable()
      .subscribe((response: any) => {
        this.timetableList = response;
      });
  }

  loadFaculty() {
    this.facultyService
      .getAllFaculty()
      .subscribe((response: any) => {
        this.facultyList = response;
      });
  }

  loadRooms() {
    this.roomService
      .getAllRooms()
      .subscribe((response: any) => {
        this.roomList = response;
      });
  }

  editEntry(entry: any) {
    this.editingTimetableId = entry.id;
    this.subjectName = entry.subjectName || '';
    this.facultyId = entry.faculty?.id || null;
    this.roomId = entry.room?.id || null;
    this.dayOfWeek = entry.dayOfWeek || '';
    this.startTime = entry.startTime || '';
    this.endTime = entry.endTime || '';

    const formCard = document.querySelector('.form-card:not(.excel-card)');
    if (formCard) {
      formCard.scrollIntoView({ behavior: 'smooth' });
    }
  }

  cancelEdit() {
    this.editingTimetableId = null;
    this.subjectName = '';
    this.facultyId = null;
    this.roomId = null;
    this.dayOfWeek = '';
    this.startTime = '';
    this.endTime = '';
  }

  saveTimetable() {
    if (!this.subjectName || !this.facultyId || !this.roomId || !this.dayOfWeek || !this.startTime || !this.endTime) {
      alert('Please fill in all fields.');
      return;
    }

    const timetable = {
      subjectName: this.subjectName,
      facultyId: this.facultyId,
      roomId: this.roomId,
      dayOfWeek: this.dayOfWeek,
      startTime: this.startTime,
      endTime: this.endTime
    };

    if (this.editingTimetableId) {
      this.timetableService
        .updateTimetable(this.editingTimetableId, timetable)
        .subscribe({
          next: () => {
            alert('Timetable entry updated successfully!');
            this.cancelEdit();
            this.loadTimetable();
          },
          error: (err) => {
            alert('Error: ' + (err.error?.message || err.message));
          }
        });
    } else {
      this.timetableService
        .addTimetable(timetable)
        .subscribe({
          next: () => {
            alert('Timetable entry added successfully!');
            this.cancelEdit();
            this.loadTimetable();
          },
          error: (err) => {
            alert('Error: ' + (err.error?.message || err.message));
          }
        });
    }
  }

  deleteEntry(id: number) {
    if (!confirm('Delete this timetable entry?')) return;
    this.timetableService.deleteTimetable(id).subscribe(() => {
      if (this.editingTimetableId === id) {
        this.cancelEdit();
      }
      this.loadTimetable();
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
    this.timetableService.uploadTimetableExcel(this.selectedFile).subscribe({
      next: (res: any) => {
        alert(res || 'Timetable imported successfully!');
        this.selectedFile = null;
        this.isUploading = false;
        this.loadTimetable();
      },
      error: (err: any) => {
        this.isUploading = false;
        const msg = err?.error || err?.message || 'Error uploading file';
        alert('Upload failed: ' + msg);
      }
    });
  }
}