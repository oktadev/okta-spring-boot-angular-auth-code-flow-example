import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Storage } from '@ionic/storage';
import { Observable } from 'rxjs/Observable';
import { forkJoin } from 'rxjs/observable/forkJoin';
import { timeoutWith } from 'rxjs/operators';
import 'rxjs/add/observable/throw';
import { OAuthService } from 'angular-oauth2-oidc';

interface Holding {
  crypto: string,
  currency: string,
  amount: number,
  value?: number
}

@Injectable()
export class HoldingsProvider {

  public holdings: Holding[] = [];
  public pricesUnavailable: boolean = false;

  constructor(private http: HttpClient, private storage: Storage, private oauthService: OAuthService) {
  }

  addHolding(holding: Holding): void {
    this.holdings.push(holding);
    this.fetchPrices();
    this.saveHoldings();
  }

  removeHolding(holding): void {
    this.holdings.splice(this.holdings.indexOf(holding), 1);
    this.fetchPrices();
    this.saveHoldings();
  }

  saveHoldings(): void {
    this.storage.set('cryptoHoldings', this.holdings);
  }

  loadHoldings(): void {
    this.storage.get('cryptoHoldings').then(holdings => {

      if (holdings !== null) {
        this.holdings = holdings;
        this.fetchPrices();
      }
    });

    this.http.get('http://localhost:8080/hello-oauth',
      {headers: new HttpHeaders().set('Authorization', this.oauthService.authorizationHeader()), responseType: 'text'}
    ).subscribe(data => console.log('response: ', data));
  }

  verifyHolding(holding): Observable<any> {
    return this.http.get('https://api.cryptonator.com/api/ticker/' + holding.crypto + '-' + holding.currency).pipe(
      timeoutWith(5000, Observable.throw(new Error('Failed to verify holding.')))
    );
  }

  fetchPrices(refresher?): void {

    this.pricesUnavailable = false;
    let requests = [];

    for (let holding of this.holdings) {
      let request = this.http.get('https://api.cryptonator.com/api/ticker/' + holding.crypto + '-' + holding.currency);
      requests.push(request);
    }

    forkJoin(requests).pipe(
      timeoutWith(5000, Observable.throw(new Error('Failed to fetch prices.')))
    ).subscribe(results => {

      results.forEach((result: any, index) => {
        this.holdings[index].value = result.ticker.price;
      });

      if (typeof(refresher) !== 'undefined') {
        refresher.complete();
      }

      this.saveHoldings();

    }, err => {

      this.pricesUnavailable = true;
      if (typeof(refresher) !== 'undefined') {
        refresher.complete();
      }
    });
  }
}
