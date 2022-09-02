import { ComponentFixture, TestBed } from '@angular/core/testing'
import { MatIconModule } from '@angular/material/icon'
import { AppRoutingModule } from '../app-routing.module'

import { EntryListItemComponent } from './entry-list-item.component'

describe('EntryListItemComponent', () => {
  let component: EntryListItemComponent
  let fixture: ComponentFixture<EntryListItemComponent>

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppRoutingModule,
        MatIconModule
      ],
      declarations: [EntryListItemComponent]
    })
      .compileComponents()

    fixture = TestBed.createComponent(EntryListItemComponent)
    component = fixture.componentInstance
    component.entry = {
      id: 'abc123',
      title: 'My first entry',
      content: '',
      author: 'Bob',
      created: '2022-09-02T16:26:18'
    }
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })

  it('should show the title of the entry', () => {
    expect(fixture.nativeElement.querySelector('.entry-title').textContent).toContain('My first entry')
  })

  it('should show the author of the entry', () => {
    expect(fixture.nativeElement.querySelector('.entry-author').textContent).toContain('Bob')
  })

  it('should show the date and time the entry was created', () => {
    expect(fixture.nativeElement.querySelector('.entry-created').textContent).toContain('9/2/22, 4:26 PM')
  })
})
