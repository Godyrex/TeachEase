import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ProfileInformationRequest} from '../../models/user/requests/ProfileInformationRequest';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {UpdatePasswordRequest} from '../../models/user/requests/UpdatePasswordRequest';
import {UserResponse} from "../../models/user/UserResponse";
import {PaginatedUsersResponse} from "../../models/user/PaginatedUsersResponse";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = 'http://localhost:8080/api/v1/user';
  image: Blob;
  storedUser;
  constructor(private http: HttpClient ) { }
  updateUserProfile(profileInfromationRequest: ProfileInformationRequest) {
    return this.http.post(`${this.baseUrl}/profile`, profileInfromationRequest);
  }
  uploadProfileImage(file: File) {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.baseUrl}/image`, formData);
  }
  getProfileImage(email: string): Observable<ArrayBuffer> {
    return this.http.get(`${this.baseUrl}/image`, { responseType: 'arraybuffer', params: {email} });
  }
  getUserProfile(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.baseUrl}/profile`);
  }
  getProfileImageBlobUrl(email: string): Observable<Blob> {
    this.storedUser = JSON.parse(sessionStorage.getItem('user'));
    if (this.image && this.storedUser.email === email) {
        return new Observable<Blob>((observer) => {
            observer.next(this.image);
            observer.complete();
        });
    }
    return this.getProfileImage(email).pipe(
        map((arrayBuffer: ArrayBuffer) => {
          const blob = new Blob([arrayBuffer], { type: 'image/jpeg' });
          if (this.storedUser.email === email) {
            this.image = blob;
          }
          return blob;
        })
    );
  }
  updatePassword(updatePasswordRequest: UpdatePasswordRequest) {
    return this.http.post(`${this.baseUrl}/updatePassword`, updatePasswordRequest);
  }

  resetPassword(updatePasswordRequest: UpdatePasswordRequest, code: string) {
    return this.http.post(`${this.baseUrl}/reset-password`, updatePasswordRequest, {params: {code}});
  }
  getUserProfileByEmail(email: string): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.baseUrl}/profile/${email}`);
  }
  setUserRole(email: string,role: string) {
    return this.http.put(`${this.baseUrl}/set-role/${email}`, null, {params: {role}});
  }
  getUsers(page: number,size: number, keyword: string): Observable<PaginatedUsersResponse> {
    return this.http.get<PaginatedUsersResponse>(`${this.baseUrl}/users`, {params: {page, size, keyword}});
  }
  getUserNameAndLastname(email: string): Observable<{name: string, lastName: string}> {
    return this.http.get<{name: string, lastName: string}>(`${this.baseUrl}/name-lastname`, {params: {email}});
  }
}
