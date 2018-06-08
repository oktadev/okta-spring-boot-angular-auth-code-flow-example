import { Component } from '@angular/core';
import { NavController, IonicPage } from 'ionic-angular';
import { HoldingsProvider } from '../../providers/holdings/holdings';
import { UserProvider } from '../../providers/user/user';

@IonicPage()
@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {
  name;

  constructor(private navCtrl: NavController, private holdingsProvider: HoldingsProvider,
              private userProvider: UserProvider) {
  }

  ionViewDidLoad(): void {
    this.userProvider.getUser().subscribe((user: any) => {
      if (user === null) {
        this.navCtrl.push('LoginPage');
      } else {
        this.name = user.name;
        this.holdingsProvider.loadHoldings();
      }
    })
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

  logout() {
    this.userProvider.logout().subscribe(() => this.navCtrl.push('LoginPage'));
  }
}
