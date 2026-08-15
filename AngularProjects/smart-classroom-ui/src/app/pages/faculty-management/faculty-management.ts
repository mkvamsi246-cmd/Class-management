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

  editingFacultyId: number | null = null;
  facultyName = '';
  department = '';
  email = '';
  password = '';

  selectedFile: File | null = null;
  isUploading = false;

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

  editFaculty(faculty: any) {
    this.editingFacultyId = faculty.id;
    this.facultyName = faculty.facultyName || '';
    this.department = faculty.department || '';
    this.email = faculty.email || '';
    this.password = ''; // Leave password blank unless updating it

    // Scroll to form card smoothly
    const formCard = document.querySelector('.form-card:not(.excel-card)');
    if (formCard) {
      formCard.scrollIntoView({ behavior: 'smooth' });
    }
  }

  cancelEdit() {
    this.editingFacultyId = null;
    this.facultyName = '';
    this.department = '';
    this.email = '';
    this.password = '';
  }

  saveFaculty() {
    if (!this.facultyName.trim() || !this.email.trim()) {
      alert('Faculty Name and Email are required.');
      return;
    }

    if (!this.editingFacultyId && !this.password.trim()) {
      alert('Password is required when adding a new faculty.');
      return;
    }

    const faculty = {
      facultyName: this.facultyName.trim(),
      department: this.department.trim(),
      email: this.email.trim(),
      password: this.password
    };

    if (this.editingFacultyId) {
      this.facultyService
        .updateFaculty(this.editingFacultyId, faculty)
        .subscribe({
          next: () => {
            alert('Faculty Updated Successfully!');
            this.cancelEdit();
            this.loadFaculty();
          },
          error: (err) => {
            const msg = err?.error || err?.message || 'Unknown error';
            alert('Failed to update faculty: ' + msg);
            console.error(err);
          }
        });
    } else {
      this.facultyService
        .createFaculty(faculty)
        .subscribe({
          next: () => {
            alert('Faculty Added Successfully!');
            this.cancelEdit();
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

  deleteFaculty(id: number) {
    if (confirm('Are you sure you want to delete this faculty?')) {
      this.facultyService.deleteFaculty(id).subscribe({
        next: () => {
          alert('Faculty deleted successfully!');
          if (this.editingFacultyId === id) {
            this.cancelEdit();
          }
          this.loadFaculty();
        },
        error: (err) => {
          alert('Failed to delete faculty: ' + (err?.error || err?.message));
        }
      });
    }
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
    this.facultyService.uploadFacultyExcel(this.selectedFile).subscribe({
      next: (res: any) => {
        alert(res || 'Faculty imported successfully!');
        this.selectedFile = null;
        this.isUploading = false;
        this.loadFaculty();
      },
      error: (err: any) => {
        this.isUploading = false;
        const msg = err?.error || err?.message || 'Error uploading file';
        alert('Upload failed: ' + msg);
      }
    });
  }
}