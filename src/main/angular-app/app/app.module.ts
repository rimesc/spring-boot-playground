import { LayoutModule } from '@angular/cdk/layout'
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http'
import { NgModule } from '@angular/core'
import { MatButtonModule } from '@angular/material/button'
import { MatIconModule } from '@angular/material/icon'
import { MatSidenavModule } from '@angular/material/sidenav'
import { MatToolbarModule } from '@angular/material/toolbar'
import { BrowserModule } from '@angular/platform-browser'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'
import { AuthHttpInterceptor, AuthModule } from '@auth0/auth0-angular'
import { MarkdownModule } from 'ngx-markdown'
import { environment } from '../environments/environment'

import { AppRoutingModule } from './app-routing.module'
import { AppComponent } from './app.component'

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    AuthModule.forRoot({
      ...environment.auth0,
      httpInterceptor: {
        allowedList: ['/api/*']
      }
    }),
    BrowserAnimationsModule,
    HttpClientModule,
    LayoutModule,
    MarkdownModule.forRoot(),
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthHttpInterceptor,
      multi: true,
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
