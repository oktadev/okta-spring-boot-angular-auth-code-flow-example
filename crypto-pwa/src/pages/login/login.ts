import { Component } from '@angular/core';
import { IonicPage } from 'ionic-angular';

@IonicPage({
  name: 'LoginPage'
})
@Component({
  selector: 'page-login',
  templateUrl: 'login.html'
})
export class LoginPage {

  login() {
    let port = (location.port ? ':' + location.port : '');
    console.log('port', port);
    if (port === ':8100') {
      port = ':8080';
    }
    location.href = '//' + location.hostname + port + '/login';
  }
}
