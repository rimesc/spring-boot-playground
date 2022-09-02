import { HttpClient } from '@angular/common/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { Entry } from './entry'

@Injectable({
  providedIn: 'root'
})
export class EntriesService {
  constructor (private http: HttpClient) { }

  listEntries (): Observable<Entry[]> {
    return this.http.get<Entry[]>('/api/journal/entries/')
  }
}
