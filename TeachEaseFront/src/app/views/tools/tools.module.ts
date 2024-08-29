import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ToolsRoutingModule } from './tools-routing.module';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Users} from "./users/users.component";
import {NgxDatatableModule} from "@swimlane/ngx-datatable";
import {NgxPaginationModule} from "ngx-pagination";


@NgModule({
  declarations: [
    Users
  ],
  imports: [
    CommonModule,
    ToolsRoutingModule,
    FormsModule,
    NgxDatatableModule,
    ReactiveFormsModule,
    NgxPaginationModule
  ]
})
export class ToolsModule { }
