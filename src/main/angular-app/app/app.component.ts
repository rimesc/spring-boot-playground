import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout'
import { Component, OnInit } from '@angular/core'
import { Observable } from 'rxjs'
import { map, shareReplay } from 'rxjs/operators'
import { EntriesService } from './entries.service'
import { Entry } from './entry'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  entries!: Observable<Entry[]>

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    )

  constructor (private entriesService: EntriesService, private breakpointObserver: BreakpointObserver) {
  }

  title: string = 'Spring Boot Playground'

  ngOnInit (): void {
    this.entries = this.entriesService.listEntries()
  }
}
