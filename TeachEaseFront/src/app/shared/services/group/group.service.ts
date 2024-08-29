import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GroupRequest } from '../../models/group/GroupRequest';
import { GroupResponse } from '../../models/group/GroupResponse';

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  private apiUrl = 'http://localhost:8080/api/v1/group';

  constructor(private http: HttpClient) {}

  createGroup(groupRequest: GroupRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, groupRequest);
  }

  getGroups(): Observable<GroupResponse[]> {
    return this.http.get<GroupResponse[]>(this.apiUrl);
  }

  getGroup(groupId: string): Observable<GroupResponse> {
    return this.http.get<GroupResponse>(`${this.apiUrl}/${groupId}`);
  }

  deleteGroup(groupId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${groupId}`);
  }

  updateGroupName(groupId: string, name: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${groupId}`, { name });
  }

  addStudentToGroup(groupId: string, studentEmail: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${groupId}/add-student`, { studentEmail });
  }

  removeStudentFromGroup(groupId: string, studentEmail: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${groupId}/remove-student`, { studentEmail });
  }
}