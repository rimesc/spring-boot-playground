import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Entry } from './entry'
import { Observable } from 'rxjs'

@Injectable({
  providedIn: 'root'
})
export class EntriesService {
  constructor (private http: HttpClient) { }

  listEntries (): Observable<Entry[]> {
    return this.http.get<Entry[]>('/api/journal/entries/')
  }
}
