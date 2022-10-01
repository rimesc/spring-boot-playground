import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { AuthGuard } from '@auth0/auth0-angular'

const routes: Routes = [
  {
    path: '',
    redirectTo: '/journal',
    pathMatch: 'full'
  },
  {
    path: 'journal',
    loadChildren: () => import('./journal/journal.module').then(m => m.JournalModule),
    canActivate: [AuthGuard]
  }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
