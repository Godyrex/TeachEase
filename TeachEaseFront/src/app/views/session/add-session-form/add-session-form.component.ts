import {Component, Input} from '@angular/core';
import {GroupResponse} from "../../../shared/models/group/GroupResponse";
import {ToastrService} from "ngx-toastr";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {
  AbstractControl,
  AsyncValidatorFn,
  FormBuilder,
  ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {SessionRequest} from "../../../shared/models/session/SessionRequest";
import {SessionService} from "../../../shared/services/session/session.service";
import {Observable, of} from "rxjs";
export function futureDateValidator(): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    const date = new Date(control.value);
    const currentDate = new Date();
    return of(date > currentDate ? null : { 'invalidDate': true });
  };
}
@Component({
  selector: 'app-add-session-form',
  templateUrl: './add-session-form.component.html',
  styleUrls: ['./add-session-form.component.scss']
})
export class AddSessionFormComponent {
  loading = false;
  files: File[] = [];
  sessionRequest: SessionRequest = {};
  @Input() groupResponse: GroupResponse;
  constructor(
      private toastr: ToastrService,
      private activeModal: NgbActiveModal,
      private formBuilder: FormBuilder,
      private sessionService: SessionService
  ) {
  }
  addSessionForm = this.formBuilder.group({
    title: ['', [Validators.required, Validators.maxLength(255), Validators.minLength(3)]],
    description: ['', [Validators.required, Validators.maxLength(255), Validators.minLength(3)]],
    scheduledTime: [new Date() ,[Validators.required], [futureDateValidator()]],
    location: ['', [Validators.minLength(3), Validators.maxLength(255)]],
    url: ['', [Validators.minLength(3), Validators.maxLength(255)]]
  }
  );
dateValidator(controlName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const date = new Date(control.value);
      const currentDate = new Date();
      if (date < currentDate) {
        return { 'invalidDate': true };
      }
      return null;
    };
}
  shouldShowErrorAddSession(controlName: string, errorName: string): boolean {
    const control = this.addSessionForm.get(controlName);
    return control && control.errors && control.errors[errorName] && (control.dirty || control.touched);
  }
  addSession() {
    if (this.addSessionForm.valid) {
      this.loading = true;
      this.sessionRequest = this.addSessionForm.value;
      this.sessionService.createSession(this.sessionRequest,this.groupResponse.id).subscribe(
          () => {
            this.toastr.success('Session added successfully');
            this.activeModal.close();
          },
          error => {
            this.toastr.error('Error adding session');
            this.loading = false;
          }
      );
    }else{
        this.toastr.error('Please fill all fields');
    }
  }
  onCancel() {
    this.activeModal.dismiss();
  }
}
