import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TimetableService {

  private apiUrl = 'http://localhost:8080/api/timetable';

  constructor(private http: HttpClient) {}

  getAllTimetable() {
    return this.http.get(this.apiUrl);
  }

  addTimetable(data: any) {
    return this.http.post(this.apiUrl, data);
  }

  getFacultyClasses(id: number) {
    return this.http.get(`${this.apiUrl}/faculty/${id}`);
  }

  deleteTimetable(id: number) {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }
}