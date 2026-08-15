import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { FacultyService } from '../../services/faculty';

@Component({
  selector: 'app-faculty-management',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './faculty-management.html',
  styleUrl: './faculty-management.css'
})
export class FacultyManagement implements OnInit {

  facultyList: any[] = [];

  facultyName = '';
  department = '';
  email = '';
  password = '';

  constructor(
    private facultyService: FacultyService
  ) {}

  ngOnInit(): void {
    this.loadFaculty();
  }

  loadFaculty() {
    this.facultyService
      .getAllFaculty()
      .subscribe((response: any) => {
        this.facultyList = response;
      });
  }

  saveFaculty() {
    if (!this.facultyName.trim() || !this.email.trim() || !this.password.trim()) {
      alert('Faculty Name, Email and Password are required.');
      return;
    }

    const faculty = {
      facultyName: this.facultyName.trim(),
      department: this.department.trim(),
      email: this.email.trim(),
      password: this.password
    };

    this.facultyService
      .createFaculty(faculty)
      .subscribe({
        next: () => {
          alert('Faculty Added Successfully!');
          // Clear the form
          this.facultyName = '';
          this.department = '';
          this.email = '';
          this.password = '';
          this.loadFaculty();
        },
        error: (err) => {
          const msg = err?.error || err?.message || 'Unknown error';
          alert('Failed to add faculty: ' + msg);
          console.error(err);
        }
      });
  }
}