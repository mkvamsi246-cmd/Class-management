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

  // Form fields
  subjectName = '';
  facultyId: number | null = null;
  roomId: number | null = null;
  dayOfWeek = '';
  startTime = '';
  endTime = '';

  days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];

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

    this.timetableService
      .addTimetable(timetable)
      .subscribe({
        next: () => {
          alert('Timetable entry added successfully!');
          this.subjectName = '';
          this.facultyId = null;
          this.roomId = null;
          this.dayOfWeek = '';
          this.startTime = '';
          this.endTime = '';
          this.loadTimetable();
        },
        error: (err) => {
          alert('Error: ' + (err.error?.message || err.message));
        }
      });
  }

  deleteEntry(id: number) {
    if (!confirm('Delete this timetable entry?')) return;
    this.timetableService.deleteTimetable(id).subscribe(() => {
      this.loadTimetable();
    });
  }
}