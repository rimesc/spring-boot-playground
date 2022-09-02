import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { EntryListComponent } from './entry-list.component'

const routes: Routes = [
  {
    path: "",
    outlet: "sidebar",
    component: EntryListComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class JournalRoutingModule { }
