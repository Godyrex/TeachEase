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

@Component({
  selector: 'app-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.scss']
})
export class GroupComponent implements OnInit{
  private routeSub: Subscription;
  id: string;
  group: GroupResponse;
  posts: PostResponse[];
  paginatedPostResponse: PaginatedPostResponse;
  user: UserResponse;
    displayTeacherInfo: string;
    imageSrc: any;
    page = 0;
    size = 1;
    teacherNameAndLastName$: Observable<{name: string, lastName: string} | null>;
  constructor(
      private route: ActivatedRoute,
      private router: Router,
      private toastr: ToastrService,
      private modalService: NgbModal,
      private groupService: GroupService,
      private sessionStorageService: SessionStorageService,
      private userService: UserService,
      private sanitizer: DomSanitizer
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
  isTeacherInGroup(){
    return this.group.teacher == this.user.email;
  }
    searchMorePosts() {
        this.page++;
        if(this.paginatedPostResponse.totalPages > this.page) {
          this.groupService.getPosts(this.id, this.page, this.size).subscribe(
              (paginatedPostResponse) => {
                  console.log('Posts:', paginatedPostResponse);
                  this.posts = this.posts.concat(paginatedPostResponse.postResponses);
                  this.paginatedPostResponse = paginatedPostResponse;
              }, error => {
                  console.error('Error fetching posts:', error);
                  this.toastr.error('Error fetching posts');
              }
          );
      }
    }
  getPosts(){
      this.groupService.getPosts(this.id, this.page, this.size).subscribe(
            (paginatedPostResponse) => {
                console.log('Posts:', paginatedPostResponse);
                this.posts = paginatedPostResponse.postResponses;
                this.paginatedPostResponse = paginatedPostResponse;
            }, error => {
                console.error('Error fetching posts:', error);
                this.toastr.error('Error fetching posts');
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
  deleteGroup(){
    this.groupService.deleteGroup(this.id).subscribe((group) => {
      this.router.navigate(['/']);
    }, (error) => {
        console.log(error);
        this.toastr.error(error.error, 'Error');
    }
    );
  }
    downloadFile(fileName: string) {
        this.groupService.downloadFile(this.group.id, fileName).subscribe(
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
          this.fetchGroupAndTeacher();
        },
        () => {
          this.fetchGroupAndTeacher();
        }
    );
  }

  deletePost(postID: string) {
    this.groupService.deletePost(this.group.id, postID).subscribe(
        () => {
          this.toastr.success('Post deleted successfully');
          this.fetchGroupAndTeacher();
        }, error => {
          console.error('Error deleting post:', error);
          this.toastr.error('Error deleting post');
        }
    );
  }

}
