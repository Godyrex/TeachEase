import {Component, Input, OnInit} from '@angular/core';
import {PresenceService} from "../../../shared/services/presence/presence.service";
import {ToastrService} from "ngx-toastr";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {GroupResponse} from "../../../shared/models/group/GroupResponse";

@Component({
  selector: 'app-view-all',
  templateUrl: './view-all.component.html',
  styleUrls: ['./view-all.component.scss']
})
export class ViewAllComponent implements OnInit {
  presenceHistory: any[] = [];
  @Input() groupResponse: GroupResponse;

  constructor(
      private presenceService: PresenceService,
      private toastr: ToastrService,
      public activeModal: NgbActiveModal
  ) {}

  ngOnInit(): void {
    this.loadPresenceHistory();
  }

  loadPresenceHistory(): void {
    this.presenceService.getPresencesByStudentAndGroup(this.groupResponse.id).subscribe(
        (data) => {
          this.presenceHistory = data;
        },
        (error) => {
          this.toastr.error('Failed to load presence history');
        }
    );
  }

  onCancel(): void {
    this.activeModal.dismiss();
  }
}
