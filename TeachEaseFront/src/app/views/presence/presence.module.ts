import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PresenceRoutingModule } from './presence-routing.module';
import { MarkPresenceComponent } from './mark-presence/mark-presence.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {SharedComponentsModule} from "../../shared/components/shared-components.module";
import { ViewAllComponent } from './view-all/view-all.component';


@NgModule({
  declarations: [
    MarkPresenceComponent,
    ViewAllComponent
  ],
  imports: [
    CommonModule,
    PresenceRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    SharedComponentsModule,
  ]
})
export class PresenceModule { }
