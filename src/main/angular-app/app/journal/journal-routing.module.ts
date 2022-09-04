import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { EntryDetailComponent } from './entry-detail.component'
import { EntryListComponent } from './entry-list.component'

const routes: Routes = [
  {
    path: '',
    outlet: 'sidebar',
    component: EntryListComponent
  },
  {
    path: 'entries/:id',
    component: EntryDetailComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class JournalRoutingModule { }
