import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout'
import { Component } from '@angular/core'
import { AuthService } from '@auth0/auth0-angular'
import { Observable } from 'rxjs'
import { map, shareReplay } from 'rxjs/operators'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    )

  constructor (private authService: AuthService, private breakpointObserver: BreakpointObserver) {
  }

  title: string = 'Spring Boot Playground'

  isLoggedIn = this.authService.isAuthenticated$

  logout () {
    this.authService.logout()
  }

  login () {
    this.authService.loginWithRedirect()
  }
}
