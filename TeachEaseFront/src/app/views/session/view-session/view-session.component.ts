import {Component, Input, OnInit} from '@angular/core';
import {SessionResponse} from "../../../shared/models/session/SessionResponse";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-view-session',
  templateUrl: './view-session.component.html',
  styleUrls: ['./view-session.component.scss']
})
export class ViewSessionComponent implements OnInit{
  loading = false;
  files: File[] = [];
  @Input() sessionResponse: SessionResponse=
      {
        id: '',
        title: '',
        description: '',
        scheduledTime: new Date(),
        location: '',
        url: '',
      };
    formattedUrl: string;

    constructor(
      private activeModal: NgbActiveModal,
  ) {
  }

    ngOnInit(): void {
        this.formattedUrl = this.formatUrl(this.sessionResponse.url);
    }
    formatUrl(url: string): string {
        if (url && !/^https?:\/\//i.test(url)) {
            return 'http://' + url;
        }
        return url;
    }
  onCancel() {
    this.activeModal.dismiss();
  }
}
