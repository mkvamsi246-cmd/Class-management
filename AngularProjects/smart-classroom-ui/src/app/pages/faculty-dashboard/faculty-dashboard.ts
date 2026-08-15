import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { RoomService } from '../../services/room';
import { RequestService } from '../../services/request';
import { TimetableService } from '../../services/timetable';

@Component({
  selector: 'app-faculty-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './faculty-dashboard.html',
  styleUrl: './faculty-dashboard.css'
})
export class FacultyDashboard implements OnInit {

  activeTab: 'overview' | 'my-classes' = 'overview';

  roomList: any[] = [];
  myClasses: any[] = [];
  requests: any[] = [];

  // Full timetable + search
  allTimetable: any[] = [];
  filteredTimetable: any[] = [];
  searchQuery = '';

  totalClasses = 0;
  pendingRequests = 0;
  approvedRequests = 0;

  facultyId = 0;

  // Swap modal
  swapModalOpen = false;
  selectedMyClass: any = null;
  selectedTargetClass: any = null;
  swapReason = '';

  constructor(
    private roomService: RoomService,
    private requestService: RequestService,
    private timetableService: TimetableService
  ) {}

  ngOnInit(): void {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    // facultyId on the Faculty entity differs from user.id;
    // the login response stores the Faculty entity's id in facultyId field
    this.facultyId = user.facultyId || user.id || 0;

    this.loadRooms();
    this.loadFacultyData();
    this.loadAllTimetable();
  }

  loadRooms() {
    this.roomService.getAllRooms().subscribe((res: any) => {
      this.roomList = res;
    });
  }

  loadFacultyData() {
    this.requestService.getFacultyRequests(this.facultyId).subscribe((data: any) => {
      this.requests = data;
      this.pendingRequests = data.filter((r: any) => r.status === 'PENDING').length;
      this.approvedRequests = data.filter((r: any) => r.status === 'APPROVED').length;
    });

    this.timetableService.getFacultyClasses(this.facultyId).subscribe((data: any) => {
      this.myClasses = data;
      this.totalClasses = data.length;
    });
  }

  loadAllTimetable() {
    this.timetableService.getAllTimetable().subscribe((data: any) => {
      this.allTimetable = data;
      this.filteredTimetable = data;
    });
  }

  onSearch() {
    const q = this.searchQuery.toLowerCase().trim();
    if (!q) {
      this.filteredTimetable = this.allTimetable;
      return;
    }
    this.filteredTimetable = this.allTimetable.filter((t: any) =>
      (t.subjectName || '').toLowerCase().includes(q) ||
      (t.dayOfWeek || '').toLowerCase().includes(q) ||
      (t.room?.roomNumber || '').toLowerCase().includes(q) ||
      (t.faculty?.facultyName || '').toLowerCase().includes(q)
    );
  }

  // ── Swap modal ──────────────────────────────────────────
  openSwap(myClass: any) {
    this.selectedMyClass = myClass;
    this.selectedTargetClass = null;
    this.swapReason = '';
    this.swapModalOpen = true;
  }

  closeSwap() {
    this.swapModalOpen = false;
    this.selectedMyClass = null;
    this.selectedTargetClass = null;
  }

  selectTarget(t: any) {
    // Cannot swap with own class
    if (t.faculty?.id === this.facultyId) return;
    this.selectedTargetClass = t;
  }

  submitSwap() {
    if (!this.selectedMyClass || !this.selectedTargetClass) {
      alert('Please select a target class to swap with.');
      return;
    }

    this.requestService
      .createSwapRequest(
        this.facultyId,
        this.selectedMyClass.id,
        this.selectedTargetClass.id,
        this.swapReason
      )
      .subscribe({
        next: () => {
          alert('Swap request submitted! Awaiting admin approval.');
          this.closeSwap();
        },
        error: (err) => {
          alert('Error: ' + (err.error || err.message));
        }
      });
  }
}