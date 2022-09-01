import { TestBed } from '@angular/core/testing'

import { EntriesService } from './entries.service'
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing'
import { Entry } from './entry'

describe('EntriesService', () => {
  let service: EntriesService
  let httpTestingController: HttpTestingController

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    })
    service = TestBed.inject(EntriesService)
    httpTestingController = TestBed.inject(HttpTestingController)
  })

  it('should be created', () => {
    expect(service).toBeTruthy()
  })

  describe('listEntries()', () => {
    it('should return a list of entries', () => {
      const testData: Entry[] = [
        { id: '1', 'title': 'My first entry', content: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.', author: 'alice', created: '2022-08-31T21:28:42.753Z' },
        { id: '2', 'title': 'My second entry', content: 'Nulla nec finibus nisi.', author: 'bob', created: '2022-08-31T21:28:42.753Z' },
        { id: '3', 'title': 'My third entry', content: 'Fusce imperdiet, felis et hendrerit lacinia, enim ipsum venenatis quam.', author: 'carol', created: '2022-08-31T21:28:42.753Z' }
      ]

      service.listEntries().subscribe(data => expect(data).toEqual(testData))

      const req = httpTestingController.expectOne('/api/journal/entries/')
      expect(req.request.method).toEqual('GET')
      req.flush(testData)
      httpTestingController.verify()
    })
  })
})
