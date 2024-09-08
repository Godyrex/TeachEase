import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {SessionResponse} from "../../../shared/models/session/SessionResponse";
import {PresenceRequest} from "../../../shared/models/presence/PresenceRequest";
import {ToastrService} from "ngx-toastr";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {PresenceService} from "../../../shared/services/presence/presence.service";
import {GroupResponse} from "../../../shared/models/group/GroupResponse";

@Component({
  selector: 'app-mark-presence',
  templateUrl: './mark-presence.component.html',
  styleUrls: ['./mark-presence.component.scss']
})
export class MarkPresenceComponent implements OnInit {
  loading = false;
  updateSessionForm: FormGroup;
  @Input() sessionResponse: SessionResponse;
  presenceRequest: PresenceRequest= {
    presences: []
  };
  @Input() groupResponse: GroupResponse;
  formattedUrl: string;


  constructor(
      private toastr: ToastrService,
      private activeModal: NgbActiveModal,
      private formBuilder: FormBuilder,
      private presenceService: PresenceService
  ) {}

  ngOnInit(): void {
    this.updateSessionForm = this.formBuilder.group({});
    this.groupResponse.students.forEach(student => {
      const presence = { student: student, present: false };
      this.presenceRequest.presences.push(presence);
      this.updateSessionForm.addControl('presence_' + student, this.formBuilder.control(presence.present, Validators.required));
    });
    this.presenceService.getPresencesBySession(this.sessionResponse.id).subscribe(presences => {
        presences.forEach(presence => {
            const controlName = 'presence_' + presence.student;
            const control = this.updateSessionForm.controls[controlName];
            if (control) {
            control.setValue(presence.present);
            }
        });
    });
    this.formattedUrl = this.formatUrl(this.sessionResponse.url);

  }

  formatUrl(url: string): string {
    if (url && !/^https?:\/\//i.test(url)) {
      return 'http://' + url;
    }
    return url;
  }
  updatePresence() {
    this.loading = true;
    console.log(this.presenceRequest);
    console.log(this.updateSessionForm.value);

    this.presenceRequest.presences.forEach(presence => {
      // Make sure you add 'presence_' prefix when accessing the form control
      const controlName = "presence_" + presence.student;
      const control = this.updateSessionForm.controls[controlName];
      console.log(this.updateSessionForm.value);
      console.log(controlName);
      if (control) {
        console.log(control.value);
        presence.present = control.value;  // Update presence with form value
      } else {
        console.log("control is null for: ", 'presence_' + presence.student);
      }
    });

    console.log(this.presenceRequest);

    this.presenceService.createPresences(this.presenceRequest, this.sessionResponse.id).subscribe(() => {
      this.toastr.success('Presence updated successfully');
      this.activeModal.close();
    }, error => {
      this.toastr.error('Failed to update presence');
      this.loading = false;
    });
  }


  onCancel() {
    this.activeModal.dismiss();
  }
}
