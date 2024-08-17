import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {ProfileInformationRequest} from '../../../shared/models/user/requests/ProfileInformationRequest';
import {UserService} from '../../../shared/services/user/user.service';
import {SessionStorageService} from '../../../shared/services/user/session-storage.service';
import {UserResponse} from '../../../shared/models/user/UserResponse';
import {NgbDateStruct} from '@ng-bootstrap/ng-bootstrap';
import {ResponseHandlerService} from '../../../shared/services/user/response-handler.service';
@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  constructor(
      private formBuilder: FormBuilder,
      private toastr: ToastrService,
      private userService: UserService,
      private sessionStorageService: SessionStorageService,
      private responseHandlerService: ResponseHandlerService
  ) {
  }
  loading: boolean;
  connectedUser: UserResponse;
    informationForm = this.formBuilder.group({
        name: [' ', [Validators.required, Validators.maxLength(20), Validators.minLength(3)]],
        lastname: ['', [Validators.required, Validators.maxLength(20), Validators.minLength(3)]]
      }
  );
  profileInfromationRequest: ProfileInformationRequest = {};
    selectedFileName: string;
    selectedFileUrl: string | ArrayBuffer;
  file: any;
  ngOnInit() {
   this.connectedUser = this.sessionStorageService.getUserFromSession();
   console.log(this.connectedUser);
      this.initializeFormWithUserData();
  }
    initializeFormWithUserData() {
        if (this.connectedUser) {
            this.informationForm.patchValue({
                name: this.connectedUser.name,
                lastname: this.connectedUser.lastname
            });
        }
    }

  updateUserProfile() {
    this.userService.updateUserProfile(this.profileInfromationRequest).subscribe(
        res => {
          this.userService.getUserProfile().subscribe(
                user => {
                    this.sessionStorageService.setUser(user);
                }
            );
          this.loading = false;
          this.toastr.success("Profile updated", 'Success!', {progressBar: true});
        },
        error => {
          this.loading = false;
          this.responseHandlerService.handleError(error);
        }
    );
  }
  updateProfileInformation() {
    this.loading = true;
    if (this.informationForm.valid) {
      this.profileInfromationRequest = this.informationForm.getRawValue();
      this.updateUserProfile();
    } else {
      this.loading = false;
      this.toastr.error('Form is invalid', 'Error!', {progressBar: true});
    }
  }
  onFileSelected(event) {
    if (event.target.files.length > 0) {
       this.file = event.target.files[0];
        const reader = new FileReader();
      this.selectedFileName = this.file.name;
      reader.onload = (e) => this.selectedFileUrl = reader.result;
        reader.readAsDataURL(this.file);
    }
  }
  uploadProfilePicture() {
    this.loading = true;
    if (!this.file) {
      this.toastr.error('No file selected', 'Error!', {progressBar: true});
      this.loading = false;
      return;
    }
    this.userService.uploadProfileImage(this.file).subscribe(
        res => {
          this.userService.getUserProfile().subscribe(
              user => {
                this.sessionStorageService.setUser(user);
              }
          );
          this.loading = false;
          this.toastr.success("Image updated", 'Success!', {progressBar: true});
        },
        error => {
          this.loading = false;
            this.responseHandlerService.handleError(error);
        }
    );
  }
}
