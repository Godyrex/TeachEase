import {Component, Input, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidatorFn, Validators} from "@angular/forms";
import {GroupRequest} from "../../../shared/models/group/GroupRequest";
import {GroupService} from "../../../shared/services/group/group.service";
import {ToastrService} from "ngx-toastr";
import {GroupResponse} from "../../../shared/models/group/GroupResponse";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-update-group-form',
  templateUrl: './update-group-form.component.html',
  styleUrls: ['./update-group-form.component.scss']
})
export class UpdateGroupFormComponent implements OnInit {
  form: FormGroup;
  @Input() groupResponse: GroupResponse = {
    id: '',
    name: '',
    students: [],
  };
  loading : boolean = false;
  constructor(
      private fb: FormBuilder,
      private groupService: GroupService,
      private toastr: ToastrService,
      public activeModal: NgbActiveModal
  ) {
    this.form = this.fb.group({
      groupName: ['', Validators.required],
      studentsEmails: ['', Validators.required],
    });
  }
  onCancel() {
    this.activeModal.dismiss();
  }
  ngOnInit(): void {
    this.form.get('groupName').setValue(this.groupResponse.name);
    this.form.get('studentsEmails').setValue(this.groupResponse.students);
    }

  onSubmit() {
    this.loading = true;
    console.log(this.groupResponse);
    if (this.form.valid) {
      this.updateGroup()
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
  updateGroup() {
    const groupRequest: GroupRequest = {
      name: this.form.get('groupName').value,
      students: this.form.get('studentsEmails').value,
    };
    this.groupService.updateGroup(this.groupResponse.id, groupRequest).subscribe(() => {
      this.toastr.success('Group updated successfully');
      this.loading = false;
    }, (error) => {
      this.toastr.error('An error occurred while updating the group');
      this.loading = false
  });
  }
}
