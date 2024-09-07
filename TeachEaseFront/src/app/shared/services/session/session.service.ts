import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {SessionResponse} from "../../models/session/SessionResponse";
import {SessionRequest} from "../../models/session/SessionRequest";

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private apiUrl = 'http://localhost:8080/api/v1/sessions';

  constructor(private http: HttpClient) {}

  getSession(sessionId: string): Observable<SessionResponse> {
    return this.http.get<SessionResponse>(`${this.apiUrl}/${sessionId}`);
  }

  getSessionByGroupId(groupId: string): Observable<SessionResponse[]> {
    return this.http.get<SessionResponse[]>(`${this.apiUrl}/group/${groupId}`);
  }
  getUpcomingSessionsByGroupId(groupId: string): Observable<SessionResponse[]> {
    return this.http.get<SessionResponse[]>(`${this.apiUrl}/group/${groupId}/upcoming`);
  }

  createSession(sessionRequest: SessionRequest, groupId: string): Observable<void> {
    return this.http.post<void>(this.apiUrl, sessionRequest, {
      params: { groupId }
    });
  }

  updateSession(sessionId: string, sessionRequest: SessionRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${sessionId}`, sessionRequest);
  }

  deleteSession(sessionId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${sessionId}`);
  }
}