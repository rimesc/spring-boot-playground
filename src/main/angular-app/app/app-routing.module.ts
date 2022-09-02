import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'

const routes: Routes = [
  {
    path: '',
    redirectTo: '/journal',
    pathMatch: 'full'
  },
  {
    path: 'journal',
    loadChildren: () => import('./journal/journal.module').then(m => m.JournalModule)
  }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
