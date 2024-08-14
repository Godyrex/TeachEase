import { Injectable } from '@angular/core';
import {UserResponse} from '../../models/user/UserResponse';
import {BehaviorSubject} from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class SessionStorageService {
  private userSubject: BehaviorSubject<UserResponse | null>;

  constructor() {
    const user = this.getUserFromSession();
    this.userSubject = new BehaviorSubject<UserResponse | null>(user);
  }
  setUser(user: UserResponse): void {
    this.userSubject.next(user);
    sessionStorage.setItem('user', JSON.stringify(user));
  }
  setAuthenticated(authenticated: boolean): void {
    sessionStorage.setItem('authenticated', JSON.stringify(authenticated));
  }
  getAuthenticated(): boolean {
    return JSON.parse(sessionStorage.getItem('authenticated'));
  }
  getUserFromSession(): UserResponse {
    const userJson = sessionStorage.getItem('user');
    return userJson ? JSON.parse(userJson) : null;
  }
  getUser() {
    return this.userSubject.asObservable();
  }
  clearUser(): void {
    this.userSubject.next(null);
    sessionStorage.removeItem('user');
  }
}
