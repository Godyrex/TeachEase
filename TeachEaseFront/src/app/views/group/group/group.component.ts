import {Component, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder} from "@angular/forms";
import {UserService} from "../../../shared/services/user/user.service";
import {DomSanitizer} from "@angular/platform-browser";
import {GroupService} from "../../../shared/services/group/group.service";
import {GroupResponse} from "../../../shared/models/group/GroupResponse";
import {UserResponse} from "../../../shared/models/user/UserResponse";
import {SessionStorageService} from "../../../shared/services/user/session-storage.service";

@Component({
  selector: 'app-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.scss']
})
export class GroupComponent implements OnInit{
  private routeSub: Subscription;
  id: string;
  group: GroupResponse;
  user: UserResponse;
  constructor(
      private route: ActivatedRoute,
      private router: Router,
      private toastr: ToastrService,
      private modalService: NgbModal,
      private groupService: GroupService,
      private sessionStorageService: SessionStorageService
  ) { }
  ngOnInit(): void {
    this.routeSub = this.route.params.subscribe(params => {
      this.id = params['id'];
      this.fetchGroup();
    });
    this.sessionStorageService.getUser().subscribe((user) => {
      this.user = user;
    })
  }
  isTeacherInGroup(){
    return this.group.teacher == this.user.email;
  }
  fetchGroup() {
    this.groupService.getGroup(this.id).subscribe((group) => {
      this.group = group;
    }, (error) => {
        console.log(error);
        this.toastr.error(error.error, 'Error');
    }
    );
  }
  deleteGroupModal(content: any) {
    this.modalService.open(content, { ariaLabelledBy: 'delete groupe' })
        .result.then((result) => {
      if (result === 'Ok') {
      this.deleteGroup();

      }
    }, (reason) => {
      console.log('Err!', reason);
    });
  }
  leaveGroupModal(content: any) {
    this.modalService.open(content, { ariaLabelledBy: 'leave group' })
        .result.then((result) => {
      if (result === 'Ok') {
      this.leaveGroup();
      }
    }, (reason) => {
      console.log('Err!', reason);
    });
  }
  leaveGroup(){
    this.groupService.leaveGroup(this.id).subscribe((group) => {
      this.router.navigate(['/']);
    }, (error) => {
        console.log(error);
        this.toastr.error(error.error, 'Error');
    }
    );
  }
  deleteGroup(){
    this.groupService.deleteGroup(this.id).subscribe((group) => {
      this.router.navigate(['/']);
    }, (error) => {
        console.log(error);
        this.toastr.error(error.error, 'Error');
    }
    );
  }
  teacherPosts = [
    { title: 'Chapter 1: Introduction to HTML', description: 'Here is an introduction to HTML basics.' },
    { title: 'Video: Bootstrap Tutorial', description: 'Watch this video to learn Bootstrap basics.' },
  ];

  sessions = [
    { title: 'HTML Basics', date: new Date(2024, 7, 26) },
    { title: 'CSS Styling', date: new Date(2024, 7, 28) },
  ];

  presenceHistory = [
    { date: new Date(2024, 7, 22), status: 'Present' },
    { date: new Date(2024, 7, 21), status: 'Absent' },
  ];

  additionalInfo = ['Class Notes', 'Assignments', 'Extra Resources'];

  newPost = { title: '', description: '' };
  newSession = { title: '', date: new Date };

  displayedColumns: string[] = ['date', 'status'];

  addPost() {
    if (this.newPost.title && this.newPost.description) {
      this.teacherPosts.push({ ...this.newPost });
      this.newPost = { title: '', description: '' };
    }
  }

  editPost(post: any) {
    // Implement your edit logic here
  }

  deletePost(post: any) {
    this.teacherPosts = this.teacherPosts.filter((p) => p !== post);
  }

  addSession() {
    if (this.newSession.title && this.newSession.date) {
      this.sessions.push(this.newSession);
      this.newSession = { title: '', date: new Date };
    }
  }

  editSession(session: any) {
    // Implement your edit logic here
  }

  deleteSession(session: any) {
    this.sessions = this.sessions.filter((s) => s !== session);
  }
}
