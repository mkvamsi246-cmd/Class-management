import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RoomService {

  private apiUrl = 'http://localhost:8080/api/rooms';

  constructor(private http: HttpClient) {}

  getAllRooms() {
    return this.http.get(this.apiUrl);
  }

  getAvailableRooms() {
    return this.http.get(`${this.apiUrl}/available`);
  }

  addRoom(room: any) {
    return this.http.post(this.apiUrl, room);
  }

  updateRoom(id: number, room: any) {
    return this.http.put(`${this.apiUrl}/${id}`, room);
  }

  deleteRoom(id: number) {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }

  uploadRoomsExcel(file: File) {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post('http://localhost:8080/api/upload/rooms', formData, { responseType: 'text' });
  }
}