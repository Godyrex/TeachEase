import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {Users} from "./users/users.component";
import {AuthGuard} from "../../shared/services/auth.guard";

const routes: Routes = [  {
  path: 'users',
  component: Users,
  canActivate: [AuthGuard],
  data: {
    roles: ['ADMIN']
  }
},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ToolsRoutingModule { }
