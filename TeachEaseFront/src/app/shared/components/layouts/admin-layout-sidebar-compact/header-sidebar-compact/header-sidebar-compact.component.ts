import { Component, OnInit } from "@angular/core";
import { NavigationService } from "src/app/shared/services/navigation.service";
import { SearchService } from "src/app/shared/services/search.service";
import {UserResponse} from "../../../../models/user/UserResponse";
import {Observable} from "rxjs";
import {SessionStorageService} from "../../../../services/user/session-storage.service";
import {UserService} from "../../../../services/user/user.service";
import {DomSanitizer} from "@angular/platform-browser";
import {AuthenticationService} from "../../../../services/user/authentication.service";
import {AddGroupFormComponent} from "../../../../../views/group/add-group-form/add-group-form.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {GroupResponse} from "../../../../models/group/GroupResponse";
import {GroupService} from "../../../../services/group/group.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: "app-header-sidebar-compact",
  templateUrl: "./header-sidebar-compact.component.html",
  styleUrls: ["./header-sidebar-compact.component.scss"]
})
export class HeaderSidebarCompactComponent implements OnInit {
  notifications: any[];

  constructor(
    private navService: NavigationService,
    public searchService: SearchService,
    private auth: AuthenticationService,
    private sessionStorageService: SessionStorageService,
    private userService: UserService,
    private groupService: GroupService,
    private sanitizer: DomSanitizer,
    private dialog: NgbModal,
    private toastr: ToastrService
  ) {
    this.notifications = [
      {
        icon: "i-Speach-Bubble-6",
        title: "New message",
        badge: "3",
        text: "James: Hey! are you busy?",
        time: new Date(),
        status: "primary",
        link: "/chat"
      },
      {
        icon: "i-Receipt-3",
        title: "New order received",
        badge: "$4036",
        text: "1 Headphone, 3 iPhone x",
        time: new Date("11/11/2018"),
        status: "success",
        link: "/tables/full"
      },
      {
        icon: "i-Empty-Box",
        title: "Product out of stock",
        text: "Headphone E67, R98, XL90, Q77",
        time: new Date("11/10/2018"),
        status: "danger",
        link: "/tables/list"
      },
      {
        icon: "i-Data-Power",
        title: "Server up!",
        text: "Server rebooted successfully",
        time: new Date("11/08/2018"),
        status: "success",
        link: "/dashboard/v2"
      },
      {
        icon: "i-Data-Block",
        title: "Server down!",
        badge: "Resolved",
        text: "Region 1: Server crashed!",
        time: new Date("11/06/2018"),
        status: "danger",
        link: "/dashboard/v3"
      }
    ];
  }

  imageSrc: any;
  user$: Observable<UserResponse | null>;
  groups: GroupResponse[] = [];
  user: UserResponse | null;
  ngOnInit() {
    this.user$ = this.sessionStorageService.getUser();
    this.user$.subscribe(user => {
      if (user && user.email) {
        this.user = user;
        this.userService.getProfileImageBlobUrl(user.email).subscribe((blob: Blob) => {
          const objectURL = URL.createObjectURL(blob);
          this.imageSrc = this.sanitizer.bypassSecurityTrustUrl(objectURL);
        });
      }
    });
    this.getMyGroups();
  }
  refresh() {
    window.location.reload();
  }
  getMyGroups() {
    this.groupService.getGroups().subscribe((groups: GroupResponse[]) => {
      this.groups = groups;
    },error => {
      console.log(error);
        this.toastr.error(error.error);
    }
    );
  }
  openAddGroupForm() {
    const dialogRef = this.dialog.open(AddGroupFormComponent);
    dialogRef.result.then(() => {
      dialogRef.close();
        console.log("Dialog closed");
    });
  }
  toggelSidebar() {
    const state = this.navService.sidebarState;
    state.sidenavOpen = !state.sidenavOpen;
    state.childnavOpen = !state.childnavOpen;
  }
  signout() {
    this.auth.logoutImpl();
  }
}
