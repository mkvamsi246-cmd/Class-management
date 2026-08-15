import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class FacultyService {

  private apiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {
  }

  getAllFaculty() {

    return this.http.get(
      `${this.apiUrl}/faculty`
    );

  }

  createFaculty(data: any) {

    return this.http.post(
      `${this.apiUrl}/faculty`,
      data,
      { responseType: 'text' }
    );

  }
}