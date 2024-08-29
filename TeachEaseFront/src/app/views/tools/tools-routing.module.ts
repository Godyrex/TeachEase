import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {Users} from "./users/users.component";

const routes: Routes = [  {
  path: 'users',
  component: Users
},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ToolsRoutingModule { }
