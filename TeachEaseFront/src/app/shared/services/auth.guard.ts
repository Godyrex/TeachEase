import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import { AuthService } from './auth.service';
import {Observable, of} from "rxjs";
import {map, switchMap} from "rxjs/operators";
import {AuthenticationService} from "./user/authentication.service";
import {SessionStorageService} from "./user/session-storage.service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
      private authService: AuthenticationService,
      private sessionService: SessionStorageService,
      private router: Router
  ) { }

  canActivate(
      next: ActivatedRouteSnapshot,
      state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const requiredRoles = next.data['roles'] as Array<string>;
    return this.authService.checkAuthState().pipe(
        switchMap(isAuthenticated => {
            console.log(" auth guard :"+isAuthenticated);
          if (!isAuthenticated) {
            return of(this.router.parseUrl('/sessions/signin'));
          }
          if (!requiredRoles) {
            return of(true);
          }
          return this.sessionService.getUser().pipe(
              map(user => {
                if (user && requiredRoles.includes(user.role)) {
                  return true;
                }
                return this.router.parseUrl('/sessions/signin');
              })
          );
        })
    );
  }
}
