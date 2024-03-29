import { ComponentFixture, TestBed } from '@angular/core/testing'
import { By } from '@angular/platform-browser'
import { ActivatedRoute } from '@angular/router'
import { MarkdownModule } from 'ngx-markdown'
import { of } from 'rxjs'
import { ActivatedRouteStub } from '../../testing/activated-route-stub'
import { EntriesService } from './entries.service'

import { EntryDetailComponent } from './entry-detail.component'

describe('EntryDetailComponent', () => {
  let component: EntryDetailComponent
  let fixture: ComponentFixture<EntryDetailComponent>
  let service: EntriesService
  let activatedRoute: ActivatedRouteStub

  beforeEach(async () => {
    const entriesService = jasmine.createSpyObj('EntriesService', ['getEntry'])
    const entry = {
      id: 'abc123',
      title: 'My first entry',
      content: `# Lorem ipsum dolor sit amet, consectetur adipiscing elit.

Phasellus quis suscipit nulla.

Aliquam cursus euismod vulputate.
`,
      author: 'Bob',
      created: '2022-09-02T16:26:18'
    }
    entriesService.getEntry.and.returnValue(of(entry))
    activatedRoute = new ActivatedRouteStub()

    await TestBed.configureTestingModule({
      declarations: [EntryDetailComponent],
      imports: [MarkdownModule.forRoot()],
      providers: [
        { provide: EntriesService, useValue: entriesService },
        { provide: ActivatedRoute, useValue: activatedRoute }
      ]
    })
      .compileComponents()

    fixture = TestBed.createComponent(EntryDetailComponent)
    component = fixture.componentInstance
    service = TestBed.inject(EntriesService)
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })

  it('should display the entry content', () => {
    activatedRoute.setParamMap({ id: 'abc123' })
    const h1: HTMLElement = fixture.debugElement.query(By.css('h1')).nativeElement
    const paragraphs: HTMLElement[] = fixture.debugElement.queryAll(By.css('p')).map(e => e.nativeElement)
    expect(h1.textContent).toEqual('Lorem ipsum dolor sit amet, consectetur adipiscing elit.')
    expect(paragraphs.map(e => e.textContent)).toEqual(['Phasellus quis suscipit nulla.', 'Aliquam cursus euismod vulputate.'])
  })
})
