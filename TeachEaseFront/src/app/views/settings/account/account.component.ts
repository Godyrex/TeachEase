import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {UserService} from '../../../shared/services/user/user.service';
import {SessionStorageService} from '../../../shared/services/user/session-storage.service';
import {UpdatePasswordRequest} from '../../../shared/models/user/requests/UpdatePasswordRequest';
import {SignupRequest} from '../../../shared/models/user/requests/SignupRequest';
import {UserResponse} from '../../../shared/models/user/UserResponse';
import {ResponseHandlerService} from '../../../shared/services/user/response-handler.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  constructor(
      private formBuilder: FormBuilder,
      private toastr: ToastrService,
      private userService: UserService,
      private sessionStorageService: SessionStorageService,
        private responseHandlerService: ResponseHandlerService
  ) { }
  loading: boolean;
  updatePasswordForm = this.formBuilder.group({
        password: ['', [Validators.required]],
        newPassword: ['', [Validators.required, Validators.maxLength(50), Validators.minLength(8)]],
        cPassword: ['', [Validators.required]],
      },
      {
        validator: this.ConfirmedValidator('newPassword', 'cPassword'),
      }
  );
  updatePasswordRequest: UpdatePasswordRequest;
  user: UserResponse;
  ngOnInit() {
      this.user = this.sessionStorageService.getUserFromSession();
    this.updatePasswordForm.controls.newPassword.valueChanges.subscribe(() => {
      this.updatePasswordForm.controls.cPassword.updateValueAndValidity();
    });
  }
  ConfirmedValidator(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];
      if (matchingControl.errors) {
        return;
      }
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({confirmedValidator: true});
      } else {
        matchingControl.setErrors(null);
      }
    };
  }
  updatePassword() {
    if (this.updatePasswordForm.invalid) {
      this.toastr.error('Please fill out the form correctly', 'Error!', {progressBar: true});
      return;
    }
    this.loading = true;
    this.updatePasswordRequest = this.updatePasswordForm.getRawValue();
    console.log(this.updatePasswordRequest);
    this.userService.updatePassword(this.updatePasswordRequest).subscribe(
        () => {
          this.toastr.success('Password updated successfully', 'Success!', {progressBar: true});
          this.updatePasswordForm.reset();
          this.loading = false;
        },
        error => {
            this.responseHandlerService.handleError(error);
            this.loading = false;
        }
    );
  }
}
