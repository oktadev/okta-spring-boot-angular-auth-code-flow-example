import { Component } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';

@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  rootPage:any = 'HomePage';

  constructor(oauthService: OAuthService) {
    console.log('oauthService.hasValidIdToken()', oauthService.hasValidIdToken());
    if (oauthService.hasValidIdToken()) {
      this.rootPage = 'HomePage';
    } else {
      this.rootPage = 'LoginPage';
    }
    console.log('rootPage', this.rootPage);
  }
}

