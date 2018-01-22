import { Component } from '@angular/core';
import { NavController, IonicPage } from 'ionic-angular';
import { HoldingsProvider } from '../../providers/holdings/holdings';
import { OAuthService } from 'angular-oauth2-oidc';

@IonicPage()
@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {

  constructor(private navCtrl: NavController, private holdingsProvider: HoldingsProvider,
              private oauthService: OAuthService) {
  }

  ionViewDidLoad(): void {
    if (!this.oauthService.hasValidIdToken()) {
      this.navCtrl.push('LoginPage');
    }
    this.holdingsProvider.loadHoldings();
  }

  addHolding(): void {
    this.navCtrl.push('AddHoldingPage');
  }

  goToCryptonator(): void {
    window.open('https://www.cryptonator.com/api', '_system');
  }

  refreshPrices(refresher): void {
    this.holdingsProvider.fetchPrices(refresher);
  }

  get name() {
    const claims: any = this.oauthService.getIdentityClaims();
    if (!claims) {
      return null;
    }
    return claims.name;
  }

  logout() {
    this.oauthService.logOut();
  }
}
