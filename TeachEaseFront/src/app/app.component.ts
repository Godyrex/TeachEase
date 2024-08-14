import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "./shared/services/user/authentication.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent   {
  title = 'bootDash';
  constructor() {}
}
