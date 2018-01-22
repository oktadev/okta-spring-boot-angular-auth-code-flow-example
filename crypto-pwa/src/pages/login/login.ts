import { Component } from '@angular/core';
import { IonicPage, App } from 'ionic-angular';
import { JwksValidationHandler, OAuthService } from 'angular-oauth2-oidc';

@IonicPage({
  name: 'LoginPage'
})
@Component({
  selector: 'page-login',
  templateUrl: 'login.html'
})
export class LoginPage {

  constructor(private oauthService: OAuthService, private app: App) {
    if (this.oauthService.hasValidIdToken()) {
      this.app.getRootNavs()[0].setRoot('HomePage');
    }

    oauthService.redirectUri = window.location.origin;
    oauthService.clientId = '0oadm6f31vyNMn6gf0h7';
    oauthService.scope = 'openid profile email';
    oauthService.issuer = 'https://dev-158606.oktapreview.com/oauth2/default';
    oauthService.tokenValidationHandler = new JwksValidationHandler();
    oauthService.loadDiscoveryDocumentAndTryLogin();
  }

  login() {
    this.oauthService.initImplicitFlow();
  }
}
