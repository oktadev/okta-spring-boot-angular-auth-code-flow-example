import { Component } from '@angular/core';
import { UserProvider } from '../providers/user/user';

@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  rootPage: any = 'HomePage';

  constructor(userProvider: UserProvider) {
    userProvider.getUser().subscribe((user) => {
      if (user == null) {
        this.rootPage = 'LoginPage';
      } else {
        this.rootPage = 'HomePage';
      }
    });
  }
}
