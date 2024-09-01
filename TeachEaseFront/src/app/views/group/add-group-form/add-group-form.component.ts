import { Component } from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidatorFn, Validators} from "@angular/forms";
import {GroupService} from "../../../shared/services/group/group.service";
import {ToastrService} from "ngx-toastr";
import {GroupRequest} from "../../../shared/models/group/GroupRequest";

@Component({
  selector: 'app-add-group-form',
  templateUrl: './add-group-form.component.html',
  styleUrls: ['./add-group-form.component.scss']
})
export class AddGroupFormComponent {
  form: FormGroup;
 groupRequest: GroupRequest;
    loading : boolean = false;
  constructor(
      private fb: FormBuilder,
      private groupService: GroupService,
      private toastr: ToastrService
  ) {
    this.form = this.fb.group({
      groupName: ['', Validators.required],
      studentsEmails: ['', Validators.required],
    });
  }
    public onSelect(item) {
        console.log('tag selected: value is ' + item);
        console.log('students emails: ' + this.form.get('studentsEmails').value);
    }
  onSubmit() {
    this.loading = true;
    if (this.form.valid) {
      this.CreateGroup()
    }else {
        this.loading = false;
      this.toastr.error('Please fill all fields');
    }
  }
    emailValidator: ValidatorFn = (control: AbstractControl) => {
        const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        const isValid = emailPattern.test(control.value);
        return isValid ? null : { 'invalidEmail': true };
    };
 CreateGroup()
  {
      console.log(this.form.get('studentsEmails').value);
    this.groupRequest = {
      name: this.form.get('groupName').value,
      students: this.form.get('studentsEmails').value
    }
    console.log(this.groupRequest);
    this.groupService.createGroup(this.groupRequest).subscribe(
        () => {
            this.form.reset();
            this.toastr.success('Group created successfully');
            this.loading = false;
        },error => {
            this.toastr.error('Error creating group');
            this.loading = false;
        });
  }
}
