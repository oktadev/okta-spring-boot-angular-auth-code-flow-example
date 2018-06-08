import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable()
export class UserProvider {

  constructor(public http: HttpClient) {
  }

  getUser() {
    return this.http.get('/api/user');
  }

  logout() {
    this.http.post('/api/logout', {});
  }
}
