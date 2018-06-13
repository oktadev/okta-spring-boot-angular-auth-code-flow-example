import { Component } from '@angular/core';
import { IonicPage, NavController } from 'ionic-angular';
import { UserProvider } from '../../providers/user/user';

@IonicPage({
  name: 'LoginPage'
})
@Component({
  selector: 'page-login',
  templateUrl: 'login.html'
})
export class LoginPage {

  constructor(private userProvider: UserProvider, navCtrl: NavController) {
    userProvider.getUser().subscribe((user) => {
      if (user !== null) {
        navCtrl.push('HomePage');
      }
    });
  }

  login() {
    this.userProvider.login();
  }
}
