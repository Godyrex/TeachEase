import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {LoginRequest} from '../../models/user/requests/LoginRequest';
import {SignupRequest} from '../../models/user/requests/SignupRequest';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {SessionStorageService} from './session-storage.service';
import {ResponseHandlerService} from './response-handler.service';
import {catchError, map, tap} from 'rxjs/operators';
import {UserService} from './user.service';
import {NavigationService} from '../navigation.service';
import {UserResponse} from "../../models/user/UserResponse";


@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private baseUrl = 'http://localhost:8080/api/v1/auth';
  constructor(private http: HttpClient,
              private router: Router,
              private toastr: ToastrService,
              private sessionStorageService: SessionStorageService,
              private responseHandlerService: ResponseHandlerService,
              private userService: UserService,
              private navigation: NavigationService) {
  }

  register(signupRequest: SignupRequest) {
    return this.http.post(`${this.baseUrl}/signup`, signupRequest);
  }

  login(loginRequest: LoginRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.baseUrl}/login`, loginRequest);
  }
  logout() {
      this.sessionStorageService.clearUser();
      this.sessionStorageService.setAuthenticated(false);
      return  this.http.get(`${this.baseUrl}/logout`);
  }
  logoutImpl() {
      this.sessionStorageService.clearUser();
      this.sessionStorageService.setAuthenticated(false);
      this.userService.image = null;
      this.userService.storedUser = null;
      this.http.get(`${this.baseUrl}/logout`).subscribe(
        res => {
            this.toastr.success('login successful', 'Success', {progressBar: true} );
          this.router.navigateByUrl('/sessions/signin');
        },
        error => {
            this.responseHandlerService.handleError(error);
            this.router.navigateByUrl('/sessions/signin');
        }
    );
  }
  verifyEmail(code: string) {
    return this.http.get(`${this.baseUrl}/verify-email`, {params: {code}});
  }
  resendVerificationEmail(email: string) {
    return this.http.get(`${this.baseUrl}/resend-verification-email`, {params: {email}});
  }
  sendResetPasswordEmail(email: string) {
    return this.http.get(`${this.baseUrl}/forgot-password`, {params: {email}});
  }
    checkAuthState(): Observable<boolean> {
        console.log('Checking Auth State');

        if (this.sessionStorageService.getAuthenticated() && this.sessionStorageService.getUserFromSession()) {
            console.log('CheckAUTH Authenticated');
            return of(true);
        }

        return this.http.get<UserResponse>(`${this.baseUrl}/check-auth`).pipe(
            tap({
                next: (response: UserResponse) => {
                    if (response) {
                        console.log('CheckAUTH Authenticated');
                        this.sessionStorageService.setUser(response);
                        this.sessionStorageService.setAuthenticated(true);
                    } else {
                        console.log('CheckAUTH Not Authenticated');
                        this.sessionStorageService.setAuthenticated(false);
                        this.sessionStorageService.clearUser();
                    }
                },
                error: () => {
                    console.log('CheckAUTH Not Authenticated');
                    this.sessionStorageService.setAuthenticated(false);
                    this.sessionStorageService.clearUser();
                }
            }),
            map(() => this.sessionStorageService.getAuthenticated()),
            catchError(() => {
                return of(false);
            })
        );
    }
    refreshMyInfo(): void {
         this.http.get<UserResponse>(`${this.baseUrl}/check-auth`).subscribe(
                response => {
                    this.sessionStorageService.setUser(response);
                    this.sessionStorageService.setAuthenticated(true);
                },
                error => {
                    this.responseHandlerService.handleError(error);
                    this.sessionStorageService.setAuthenticated(false);
                    this.sessionStorageService.clearUser();
                    this.router.navigateByUrl('/sessions/signin');
                }
            );
    }
    refreshPageInfo(): void {
      this.refreshMyInfo();
      this.navigation.setDefaultMenu();
    }
}
