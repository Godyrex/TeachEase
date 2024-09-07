import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SessionRoutingModule } from './session-routing.module';
import { AddSessionFormComponent } from './add-session-form/add-session-form.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {SharedComponentsModule} from "../../shared/components/shared-components.module";


@NgModule({
  declarations: [
    AddSessionFormComponent
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
