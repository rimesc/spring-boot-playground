import { Component, Input } from '@angular/core'
import { Entry } from './entry'

@Component({
  selector: 'app-entry-list-item',
  templateUrl: './entry-list-item.component.html',
  styleUrls: ['./entry-list-item.component.scss']
})
export class EntryListItemComponent {
  @Input() entry!: Entry
}
