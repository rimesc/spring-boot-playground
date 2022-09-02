import { HttpClientModule } from '@angular/common/http'
import { ComponentFixture, TestBed } from '@angular/core/testing'
import { MatListModule } from '@angular/material/list'

import { EntryListComponent } from './entry-list.component'

describe('EntryListComponent', () => {
  let component: EntryListComponent
  let fixture: ComponentFixture<EntryListComponent>

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EntryListComponent],
      imports: [
        HttpClientModule,
        MatListModule
      ]
    })
      .compileComponents()

    fixture = TestBed.createComponent(EntryListComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
