import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SessionRoutingModule } from './session-routing.module';
import { AddSessionFormComponent } from './add-session-form/add-session-form.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {SharedComponentsModule} from "../../shared/components/shared-components.module";
import { UpdateSessionFormComponent } from './update-session-form/update-session-form.component';


@NgModule({
  declarations: [
    AddSessionFormComponent,
    UpdateSessionFormComponent
  ],
  imports: [
    CommonModule,
    SessionRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    SharedComponentsModule,
  ]
})
export class SessionModule { }
