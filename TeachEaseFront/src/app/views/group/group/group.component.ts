import {Component, OnInit} from '@angular/core';
import {Observable, of, Subscription} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {GroupService} from "../../../shared/services/group/group.service";
import {GroupResponse} from "../../../shared/models/group/GroupResponse";
import {UserResponse} from "../../../shared/models/user/UserResponse";
import {SessionStorageService} from "../../../shared/services/user/session-storage.service";
import {UpdateGroupFormComponent} from "../update-group-form/update-group-form.component";
import {UserService} from "../../../shared/services/user/user.service";
import {DomSanitizer} from "@angular/platform-browser";
import {AddPostFormComponent} from "../add-post-form/add-post-form.component";
import {catchError, map, switchMap} from "rxjs/operators";
import {PostResponse} from "../../../shared/models/group/PostResponse";
import {PaginatedPostResponse} from "../../../shared/models/group/PaginatedPostResponse";
import {AddSessionFormComponent} from "../../session/add-session-form/add-session-form.component";
import {SessionService} from "../../../shared/services/session/session.service";
import {SessionResponse} from "../../../shared/models/session/SessionResponse";
import {UpdateSessionFormComponent} from "../../session/update-session-form/update-session-form.component";
import {ViewSessionComponent} from "../../session/view-session/view-session.component";
import {PresenceResponse} from "../../../shared/models/presence/PresenceResponse";
import {PresenceService} from "../../../shared/services/presence/presence.service";
import {PresenceRequest} from "../../../shared/models/presence/PresenceRequest";
import {MarkPresenceComponent} from "../../presence/mark-presence/mark-presence.component";
import {ViewAllComponent} from "../../presence/view-all/view-all.component";

@Component({
  selector: 'app-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.scss']
})
export class GroupComponent implements OnInit{
  private routeSub: Subscription;
  id: string;
  loadingStudentPresences = false;
    loadingSessions = false;
  loadingPosts = false;
  loadingExtraPosts = false;
  group: GroupResponse;
  posts: PostResponse[];
  paginatedPostResponse: PaginatedPostResponse;
  user: UserResponse;
  allSessions = false;
  sessionResponses: SessionResponse[];
  studentPresences: PresenceResponse[];
    displayTeacherInfo: string;
    imageSrc: any;
    page = 0;
    size = 3;
    teacherNameAndLastName$: Observable<{name: string, lastName: string} | null>;
  constructor(
      private route: ActivatedRoute,
      private router: Router,
      private toastr: ToastrService,
      private modalService: NgbModal,
      private groupService: GroupService,
      private sessionStorageService: SessionStorageService,
      private userService: UserService,
      private sanitizer: DomSanitizer,
      private sessionService: SessionService,
      private presenceService: PresenceService
  ) { }
  ngOnInit(): void {
    this.routeSub = this.route.params.subscribe(params => {
      this.id = params['id'];
      this.fetchGroupAndTeacher();
    });
    this.sessionStorageService.getUser().subscribe((user) => {
      this.user = user;
    })
      this.getPosts();
    this.getSessionUpcoming();
    this.getLatestPresence();
  }
    openMarkPresence(session: SessionResponse) {
        const dialogRef = this.modalService.open(MarkPresenceComponent, {
        });
        dialogRef.componentInstance.sessionResponse = session;
        dialogRef.componentInstance.groupResponse = this.group;
    }
  openUpdateGroupForm() {
    const dialogRef = this.modalService.open(UpdateGroupFormComponent, {
    });
    dialogRef.componentInstance.groupResponse = this.group;
    dialogRef.result.then(
        () => {
          this.fetchGroupAndTeacher();
        },
        () => {
          this.fetchGroupAndTeacher();
        }
    );
  }

    openUpdateSessionForm(sessionResponse: SessionResponse) {
        const dialogRef = this.modalService.open(UpdateSessionFormComponent, {
        });
        dialogRef.componentInstance.sessionResponse = sessionResponse;
        dialogRef.result.then(
            () => {
                if(this.allSessions){
                    this.getAllSessions();
                }else{
                    this.getSessionUpcoming();
                }
                },
            () => {
                if(this.allSessions){
                    this.getAllSessions();
                }else{
                    this.getSessionUpcoming();
                }
            }
        );
    }
  openAddSessionForm() {
      const dialogRef = this.modalService.open(AddSessionFormComponent, {
        });
        dialogRef.componentInstance.groupResponse = this.group;
        dialogRef.result.then(
            () => {
                if(this.allSessions){
                    this.getAllSessions();
                }else{
                    this.getSessionUpcoming();
                }
                },
            () => {
                if(this.allSessions){
                    this.getAllSessions();
                }else{
                    this.getSessionUpcoming();
                }
            }
        );
  }
  openViewSession(sessionResponse: SessionResponse) {
    const dialogRef = this.modalService.open(ViewSessionComponent, {
    });
    dialogRef.componentInstance.sessionResponse = sessionResponse;
    }getSessionUpcoming() {
        this.loadingSessions = true;
        this.sessionService.getUpcomingSessionsByGroupId(this.id).subscribe((sessions) => {
            console.log('Sessions:', sessions);
            this.sessionResponses = sessions.sort((a, b) => new Date(a.scheduledTime).getTime() - new Date(b.scheduledTime).getTime());
            this.loadingSessions = false;
            this.allSessions = false;
        }, (error) => {
            console.error('Error fetching sessions:', error);
            this.toastr.error('Error fetching sessions');
            this.loadingSessions = false;
        });
    }
    getLatestPresence(){
        this.presenceService.getLatestPresenceByStudentAndGroup(this.id).subscribe((presence) => {
            console.log('Latest Presence:', presence);
            this.studentPresences = presence;
        }, (error) => {
            console.error('Error fetching latest presence:', error);
            this.toastr.error('Error fetching latest presence');
        });
    }
    getStudentPresence(){
        this.presenceService.getPresencesByStudentAndGroup(this.id).subscribe((presences) => {
            console.log('Presences:', presences);
            this.studentPresences = presences;
        }, (error) => {
            console.error('Error fetching presences:', error);
            this.toastr.error('Error fetching presences');
        });
    }
    getAllSessions() {
        this.loadingSessions = true;
        this.sessionService.getSessionByGroupId(this.id).subscribe((sessions) => {
            console.log('Sessions:', sessions);
            this.sessionResponses = sessions.sort((a, b) => new Date(a.scheduledTime).getTime() - new Date(b.scheduledTime).getTime());
            this.loadingSessions = false;
            this.allSessions = true;
        }, (error) => {
            console.error('Error fetching sessions:', error);
            this.toastr.error('Error fetching sessions');
            this.loadingSessions = false;
        });
    }
    isSooner(scheduledTime: Date): boolean {
        const now = new Date();
        const sessionTime = new Date(scheduledTime);
        return sessionTime.getTime() - now.getTime() <= (2 * 24 * 60 * 60 * 1000) && sessionTime > now; // Within 2 days and in the future
    }

    isLater(scheduledTime: Date): boolean {
        const now = new Date();
        const sessionTime = new Date(scheduledTime);
        return sessionTime.getTime() - now.getTime() > (2 * 24 * 60 * 60 * 1000); // More than 2 days away
    }

    isPassed(scheduledTime: Date): boolean {
        const now = new Date();
        const sessionTime = new Date(scheduledTime);
        return sessionTime < now; // Already happened
    }


    isTeacherInGroup(){
    return this.group.teacher == this.user.email;
  }
    isStudentInGroup(){
        return this.group.students.includes(this.user.email);
    }
    searchMorePosts() {
        if(this.paginatedPostResponse && this.paginatedPostResponse.totalPages > this.page+1) {
            this.loadingExtraPosts = true;
            this.page++;
            this.groupService.getPosts(this.id, this.page, this.size).subscribe(
              (paginatedPostResponse) => {
                  console.log('Posts:', paginatedPostResponse);
                  this.posts = this.posts.concat(paginatedPostResponse.postResponses);
                  this.paginatedPostResponse = paginatedPostResponse;
                    this.loadingExtraPosts = false;
              }, error => {
                  console.error('Error fetching posts:', error);
                  this.toastr.error('Error fetching posts');
                    this.loadingExtraPosts = false;
              }
          );
      }
    }
  getPosts(){
      this.loadingPosts = true;
      this.groupService.getPosts(this.id, this.page, this.size).subscribe(
            (paginatedPostResponse) => {
                console.log('Posts:', paginatedPostResponse);
                this.posts = paginatedPostResponse.postResponses;
                this.paginatedPostResponse = paginatedPostResponse;
                this.loadingPosts = false;
            }, error => {
                console.error('Error fetching posts:', error);
                this.toastr.error('Error fetching posts');
                this.loadingPosts = false;
            }
        );
  }
    fetchGroupAndTeacher() {
        this.groupService.getGroup(this.id).pipe(
            catchError(error => {
                this.router.navigate(['/']);
                return of(null);
            }),
            switchMap((group) => {
                if (!group) {
                    return of(null);
                }

                this.group = group;
                if (this.group.teacher) {
                    // Fetch the teacher's profile image
                    this.userService.getProfileImageBlobUrl(this.group.teacher).subscribe((blob: Blob) => {
                        const objectURL = URL.createObjectURL(blob);
                        this.imageSrc = this.sanitizer.bypassSecurityTrustUrl(objectURL);
                    });

                    // Return the teacher's name and last name observable
                    return this.userService.getUserNameAndLastname(this.group.teacher).pipe(
                        catchError(() => {
                            console.log('Error fetching teacher name and last name');
                            this.displayTeacherInfo = this.group.teacher;
                            return of(null);
                        })
                    );
                }

                return of(null); // No teacher available
            })
        ).subscribe((nameAndLastName) => {
            if (nameAndLastName) {
                console.log('Teacher name and last name:', nameAndLastName);
                this.displayTeacherInfo = `${nameAndLastName.name} ${nameAndLastName.lastname}`;
                console.log('Teacher:', this.displayTeacherInfo);
            }
        });
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
    deletePostModal(content: any, postID: string) {
        this.modalService.open(content, { ariaLabelledBy: 'delete post' })
            .result.then((result) => {
            if (result === 'Ok') {
                this.deletePost(postID);
            }
        }, (reason) => {
            console.log('Err!', reason);
        });
    }
  deletePost(postID: string)
  {
    this.groupService.deletePost(this.id, postID).subscribe((group) => {
      this.posts = this.posts.filter(post => post.id !== postID);
    }, (error) => {
        console.log(error);
        this.toastr.error(error.error, 'Error');
    }
    );
  }
  deleteSessionModal(content: any, sessionID: string) {
    this.modalService.open(content, { ariaLabelledBy: 'delete session' })
        .result.then((result) => {
      if (result === 'Ok') {
      this.deleteSession(sessionID);
      }
    }, (reason) => {
      console.log('Err!', reason);
    });
  }
  deleteSession(sessionID: string){
    this.sessionService.deleteSession(sessionID).subscribe(() => {
        if(this.allSessions){
            this.getAllSessions();
        }else{
      this.getSessionUpcoming();
        }
      this.toastr.success('Session deleted successfully');
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
    downloadFile(fileName: string, postId: string) {
        this.groupService.downloadFile(this.group.id,postId, fileName).subscribe(
            response => {
                const blob = new Blob([response]);

                // Create a link element
                const link = document.createElement('a');

                // Set the download attribute with the filename
                link.href = window.URL.createObjectURL(blob);
                link.download = fileName;

                // Append the link to the body
                document.body.appendChild(link);

                // Programmatically click the link to trigger the download
                link.click();

                // Clean up by removing the link from the document
                document.body.removeChild(link);

                // Revoke the object URL to release memory
                window.URL.revokeObjectURL(link.href);
            }, error => {
                console.error('Error downloading file:', error);
                this.toastr.error('Error downloading file');
            });
    }
  addPostForm() {
    const dialogRef = this.modalService.open(AddPostFormComponent, {
    });
    dialogRef.componentInstance.groupResponse = this.group;
    dialogRef.result.then(
        () => {
            this.page = 0;
            this.getPosts();
        },
        () => {
            this.page = 0;
            this.getPosts();
        }
    );
  }
    ViewAllPresences() {
        const dialogRef = this.modalService.open(ViewAllComponent, {
        });
        dialogRef.componentInstance.groupResponse = this.group;
    }


}
