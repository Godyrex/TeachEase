import {Component, Input} from '@angular/core';
import {GroupResponse} from "../../../shared/models/group/GroupResponse";
import {PostRequest} from "../../../shared/models/group/PostRequest";
import {GroupService} from "../../../shared/services/group/group.service";
import {ToastrService} from "ngx-toastr";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, Validators} from "@angular/forms";

@Component({
  selector: 'app-add-post-form',
  templateUrl: './add-post-form.component.html',
  styleUrls: ['./add-post-form.component.scss']
})
export class AddPostFormComponent {
  loading = false;
  files: File[] = [];
  postRequest: PostRequest = {};
  @Input() groupResponse: GroupResponse;
  constructor(
      private groupService: GroupService,
      private toastr: ToastrService,
      private activeModal: NgbActiveModal,
      private formBuilder: FormBuilder
  ) {
  }
  addPostForm = this.formBuilder.group({
    title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    content: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(1000)]],
  });
  shouldShowErrorAddPost(controlName: string, errorName: string): boolean {
    const control = this.addPostForm.get(controlName);
    return control && control.errors && control.errors[errorName] && (control.dirty || control.touched);
  }
  addPost() {
    this.loading = true;
    if (this.addPostForm.valid) {
      this.postRequest.title = this.addPostForm.controls['title'].value;
      this.postRequest.content = this.addPostForm.controls['content'].value;
      console.log(this.postRequest);
      this.groupService.addPost(this.groupResponse.id, this.postRequest, this.files).subscribe(
          () => {
            this.toastr.success('Post added successfully');
            this.loading = false;
            this.addPostForm.reset();
            this.files = [];
            const fileInput = document.getElementById('files') as HTMLInputElement;
            if (fileInput) {
              fileInput.value = '';
            }
          }, error => {
            console.error('Error adding post:', error);
            this.toastr.error('Error adding post');
            this.loading = false;
            this.files = [];
            const fileInput = document.getElementById('files') as HTMLInputElement;
            if (fileInput) {
              fileInput.value = '';
            }
          }
      );
    } else {
      this.toastr.error('Please fill all fields correctly');
      this.loading = false;
    }
  }

  onFileSelected(event) {
    this.files = [];
    if (event.target.files.length > 0) {
      for (let i = 0; i < event.target.files.length; i++) {
        this.files.push(event.target.files[i]);
      }
    }
  }
  onCancel() {
    this.activeModal.dismiss();
  }
}
