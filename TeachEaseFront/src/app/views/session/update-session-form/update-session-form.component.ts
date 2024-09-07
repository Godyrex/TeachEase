import {Component, Input, OnInit} from '@angular/core';
import {SessionRequest} from "../../../shared/models/session/SessionRequest";
import {GroupResponse} from "../../../shared/models/group/GroupResponse";
import {ToastrService} from "ngx-toastr";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, Validators} from "@angular/forms";
import {SessionService} from "../../../shared/services/session/session.service";
import {SessionResponse} from "../../../shared/models/session/SessionResponse";

@Component({
  selector: 'app-update-session-form',
  templateUrl: './update-session-form.component.html',
  styleUrls: ['./update-session-form.component.scss']
})
export class UpdateSessionFormComponent implements OnInit {
  loading = false;
  files: File[] = [];
  sessionRequest: SessionRequest = {};
  @Input() sessionResponse: SessionResponse=
  {
    id: '',
    title: '',
    description: '',
    scheduledTime: new Date(),
    location: '',
    url: '',
  };
  constructor(
      private toastr: ToastrService,
      private activeModal: NgbActiveModal,
      private formBuilder: FormBuilder,
      private sessionService: SessionService
  ) {
  }

  ngOnInit(): void {
    this.updateSessionForm.get('title').setValue(this.sessionResponse.title);
    this.updateSessionForm.get('description').setValue(this.sessionResponse.description);
    this.updateSessionForm.get('scheduledTime').setValue(this.sessionResponse.scheduledTime);
    this.updateSessionForm.get('location').setValue(this.sessionResponse.location);
    this.updateSessionForm.get('url').setValue(this.sessionResponse.url);
    }
  updateSessionForm = this.formBuilder.group({
    title: ['', [Validators.required, Validators.maxLength(255), Validators.minLength(3)]],
    description: ['', [Validators.required, Validators.maxLength(255), Validators.minLength(3)]],
    scheduledTime: [new Date(),[Validators.required]],
    location: ['', [Validators.minLength(3), Validators.maxLength(255)]],
    url: ['', [Validators.minLength(3), Validators.maxLength(255)]]
  });
  shouldShowErrorUpdateSession(controlName: string, errorName: string): boolean {
    const control = this.updateSessionForm.get(controlName);
    return control && control.errors && control.errors[errorName] && (control.dirty || control.touched);
  }
 updateSession() {
    this.loading = true;
    if (this.updateSessionForm.valid) {
      this.sessionRequest.title = this.updateSessionForm.controls['title'].value;
      this.sessionRequest.description = this.updateSessionForm.controls['description'].value;
      this.sessionRequest.scheduledTime = this.updateSessionForm.controls['scheduledTime'].value;
      this.sessionRequest.location = this.updateSessionForm.controls['location'].value;
      this.sessionRequest.url = this.updateSessionForm.controls['url'].value;
      console.log(this.sessionRequest);
      this.sessionService.updateSession(this.sessionResponse.id, this.sessionRequest).subscribe(
          () => {
            this.toastr.success('Session updated successfully');
            this.loading = false;
            this.updateSessionForm.reset();
          }, error => {
            console.error('Error updating session:', error);
            this.toastr.error('Error updating session');
            this.loading = false;
          }
      );
    } else {
      this.loading = false;
      this.toastr.error('Please fill all fields');
      }
 }
  onCancel() {
    this.activeModal.dismiss();
  }
}
