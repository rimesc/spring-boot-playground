import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, ParamMap } from '@angular/router'
import { Observable, switchMap } from 'rxjs'
import { EntriesService } from './entries.service'
import { Entry } from './entry'

@Component({
  selector: 'app-entry-detail',
  templateUrl: './entry-detail.component.html',
  styleUrls: ['./entry-detail.component.scss']
})
export class EntryDetailComponent implements OnInit {
  entry$!: Observable<Entry>

  constructor(private route: ActivatedRoute, private entriesService: EntriesService) { }

  ngOnInit(): void {
    this.entry$ = this.route.paramMap.pipe(
      switchMap((params: ParamMap) => this.entriesService.getEntry(params.get('id')!))
    );
  }

  paragraphs(entry: Entry): string[] {
    return entry.content.split("\n\n")
  }

}
