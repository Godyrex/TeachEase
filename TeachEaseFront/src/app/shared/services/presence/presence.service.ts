import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {PresenceRequest} from "../../models/presence/PresenceRequest";
import {Observable} from "rxjs";
import {PresenceResponse} from "../../models/presence/PresenceResponse";

@Injectable({
  providedIn: 'root'
})
export class PresenceService {
  private apiUrl = 'http://localhost:8080/api/v1/presence';

  constructor(private http: HttpClient) {}

  createPresences(presenceRequest: PresenceRequest, sessionId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/create`, presenceRequest, {params: {sessionId}});
  }

  getPresencesBySession(sessionId: string): Observable<PresenceResponse[]> {
    return this.http.get<PresenceResponse[]>(`${this.apiUrl}/session/${sessionId}`);
  }

  getPresencesByStudentAndGroup(groupId: string): Observable<PresenceResponse[]> {
    return this.http.get<PresenceResponse[]>(`${this.apiUrl}/student/group/${groupId}`);
  }
  getLatestPresenceByStudentAndGroup(groupId: string): Observable<PresenceResponse[]> {
    return this.http.get<PresenceResponse[]>(`${this.apiUrl}/student-latest/group/${groupId}`);
  }
}
