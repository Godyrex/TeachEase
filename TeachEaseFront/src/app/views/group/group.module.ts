import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GroupRoutingModule } from './group-routing.module';
import { GroupComponent } from './group/group.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { AddGroupFormComponent } from './add-group-form/add-group-form.component';
import {TagInputModule} from "ngx-chips";
import {SharedComponentsModule} from "../../shared/components/shared-components.module";
import { UpdateGroupFormComponent } from './update-group-form/update-group-form.component';
import { AddPostFormComponent } from './add-post-form/add-post-form.component';
import {InfiniteScrollModule} from "ngx-infinite-scroll";


@NgModule({
  declarations: [
    GroupComponent,
    AddGroupFormComponent,
    UpdateGroupFormComponent,
    AddPostFormComponent,
  ],
    imports: [
        CommonModule,
        GroupRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        TagInputModule,
        SharedComponentsModule,
        InfiniteScrollModule,
    ]
})
export class GroupModule { }
