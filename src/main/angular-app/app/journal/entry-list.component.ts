import { Component, OnInit } from '@angular/core'
import { Observable } from 'rxjs'
import { EntriesService } from './entries.service'
import { Entry } from './entry'

/**
 * A list of journal entries, intended for the sidebar.
 */
@Component({
  selector: 'app-entry-list',
  templateUrl: './entry-list.component.html',
  styleUrls: ['./entry-list.component.scss']
})
export class EntryListComponent implements OnInit {
  entries!: Observable<Entry[]>

  constructor(private entriesService: EntriesService) { }

  ngOnInit (): void {
    this.entries = this.entriesService.listEntries()
  }
}
