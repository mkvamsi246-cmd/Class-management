import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RequestService {

  private apiUrl = 'http://localhost:8080/api/requests';

  constructor(private http: HttpClient) {}

  getRequests() {
    return this.http.get(this.apiUrl);
  }

  createRequest(data: any) {
    return this.http.post(this.apiUrl, data);
  }

  createUnoccupiedRequest(facultyId: number, roomId: number, requestDate: string,
                           startTime: string, endTime: string, reason: string) {
    return this.http.post(this.apiUrl, {
      facultyId,
      roomId,
      requestDate,
      startTime,
      endTime,
      reason,
      requestType: 'UNOCCUPIED'
    });
  }

  createSwapRequest(facultyId: number, requesterTimetableId: number,
                    targetTimetableId: number, reason: string) {
    return this.http.post(this.apiUrl, {
      facultyId,
      requesterTimetableId,
      targetTimetableId,
      reason,
      requestType: 'SWAP'
    });
  }

  approveRequest(id: number) {
    return this.http.put(`${this.apiUrl}/${id}/approve`, {});
  }

  rejectRequest(id: number) {
    return this.http.put(`${this.apiUrl}/${id}/reject`, {});
  }

  getFacultyRequests(facultyId: number) {
    return this.http.get(`${this.apiUrl}/faculty/${facultyId}`);
  }
}