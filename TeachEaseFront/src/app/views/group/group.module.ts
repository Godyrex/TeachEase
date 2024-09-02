import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GroupRoutingModule } from './group-routing.module';
import { GroupComponent } from './group/group.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { AddGroupFormComponent } from './add-group-form/add-group-form.component';
import {MatDialogModule} from "@angular/material/dialog";
import {TagInputModule} from "ngx-chips";
import {CustomFormsModule} from "ngx-custom-validators";
import {SharedComponentsModule} from "../../shared/components/shared-components.module";
import { UpdateGroupFormComponent } from './update-group-form/update-group-form.component';


@NgModule({
  declarations: [
    GroupComponent,
    AddGroupFormComponent,
    UpdateGroupFormComponent,
  ],
    imports: [
        CommonModule,
        GroupRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        TagInputModule,
        SharedComponentsModule,
    ]
})
export class GroupModule { }
