import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import { MatIconModule } from '@angular/material/icon'
import { MatListModule } from '@angular/material/list'
import { MatTooltipModule } from '@angular/material/tooltip'
import { MarkdownModule } from 'ngx-markdown'
import { EntryDetailComponent } from './entry-detail.component'
import { EntryListItemComponent } from './entry-list-item.component'
import { EntryListComponent } from './entry-list.component'

import { JournalRoutingModule } from './journal-routing.module'

@NgModule({
  declarations: [
    EntryListComponent,
    EntryListItemComponent,
    EntryDetailComponent
  ],
  imports: [
    CommonModule,
    JournalRoutingModule,
    MarkdownModule.forChild(),
    MatIconModule,
    MatListModule,
    MatTooltipModule
  ]
})
export class JournalModule {
}
