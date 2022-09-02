import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import { MatListModule } from '@angular/material/list'
import { MatTooltipModule } from '@angular/material/tooltip'
import { EntryListComponent } from './entry-list.component'

import { JournalRoutingModule } from './journal-routing.module'

@NgModule({
  declarations: [
    EntryListComponent
  ],
  imports: [
    CommonModule,
    JournalRoutingModule,
    MatListModule,
    MatTooltipModule
  ]
})
export class JournalModule {
}
