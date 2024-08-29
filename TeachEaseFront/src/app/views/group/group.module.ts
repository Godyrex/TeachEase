import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GroupRoutingModule } from './group-routing.module';
import { GroupComponent } from './group/group.component';
import {FormsModule} from "@angular/forms";
import {MatListModule} from "@angular/material/list";
import {MatLineModule} from "@angular/material/core";
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {MatTabsModule} from "@angular/material/tabs";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {MatTableModule} from "@angular/material/table";


@NgModule({
  declarations: [
    GroupComponent
  ],
  imports: [
    CommonModule,
    GroupRoutingModule,
    FormsModule,
    MatTabsModule,
    MatListModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatCardModule,
    MatTableModule,
    MatListModule,
    MatLineModule,
  ]
})
export class GroupModule { }
