import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class FacultyService {

  private apiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  getAllFaculty() {
    return this.http.get(`${this.apiUrl}/faculty`);
  }

  createFaculty(data: any) {
    return this.http.post(`${this.apiUrl}/faculty`, data, { responseType: 'text' });
  }

  updateFaculty(id: number, data: any) {
    return this.http.put(`${this.apiUrl}/faculty/${id}`, data, { responseType: 'text' });
  }

  deleteFaculty(id: number) {
    return this.http.delete(`${this.apiUrl}/faculty/${id}`, { responseType: 'text' });
  }

  uploadFacultyExcel(file: File) {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post('http://localhost:8080/api/upload/faculty', formData, { responseType: 'text' });
  }
}