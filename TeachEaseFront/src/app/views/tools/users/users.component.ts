import {Component, OnInit} from '@angular/core';
import {UserService} from "../../../shared/services/user/user.service";
import {ResponseHandlerService} from "../../../shared/services/user/response-handler.service";
import {debounceTime} from "rxjs/operators";
import {FormControl} from "@angular/forms";
import {UserResponse} from "../../../shared/models/user/UserResponse";
import {PaginatedUsersResponse} from "../../../shared/models/user/PaginatedUsersResponse";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class Users implements OnInit {
  users: UserResponse[] = [];
  _currentPage = 1;
  totalPages = 0;
  totalItems = 0;
  itemsPerPage = 5;
  loading = false;
  availableRoles: string[] = [ 'ADMIN', 'STUDENT', 'TEACHER'];
  searchControl: FormControl = new FormControl();

  get currentPage(): number {
    return this._currentPage;
  }

  set currentPage(value: number) {
    this._currentPage = value;
    if (this.searchControl.value == null) {
      this.loadUsers(this._currentPage, this.itemsPerPage, '');
    } else {
      this.loadUsers(this._currentPage, this.itemsPerPage, this.searchControl.value);
    }
  }
  constructor(
      private userService: UserService,
      private handleResponse: ResponseHandlerService,
      private toastr: ToastrService
  ) { }

  ngOnInit() {
    this.loadUsers(this.currentPage, this.itemsPerPage, '');
    this.searchControl.valueChanges
        .pipe(debounceTime(200))
        .subscribe(value => {
          this.loadUsers(1, this.itemsPerPage, value);
        });

  }
  loadUsers(page: number, size: number, keyword: string) {
    this.loading = true;
    this.userService.getUsers(page - 1, size, keyword).subscribe((response: PaginatedUsersResponse) => {
          console.log(response);
          this.users = response.users;
          this._currentPage = response.currentPage + 1;
          this.totalPages = response.totalPages;
          this.totalItems = response.totalItems;
          this.itemsPerPage = response.itemsPerPage;
          this.loading = false;
        }, error => {
          this.handleResponse.handleError(error);
          this.loading = false;
        }
    );
  }
 onRoleChange(user: UserResponse) {
    this.userService.setUserRole(user.email, user.role).subscribe(() => {
        this.toastr.success(user.email+' set as '+user.role);
      this.loadUsers(this.currentPage, this.itemsPerPage, this.searchControl.value!= null ? this.searchControl.value : '');
    }, error => {
      this.handleResponse.handleError(error);
    });
  }
}