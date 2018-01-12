import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
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

  public HOLDINGS_API = 'http://localhost:8080/api/holdings';
  public holdings: Holding[] = [];
  public pricesUnavailable: boolean = false;

  constructor(private http: HttpClient, private oauthService: OAuthService) {
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

  onError(error): void {
    console.error('ERROR: ', error);
  }

  getHeaders(): HttpHeaders {
    return new HttpHeaders().set('Authorization', this.oauthService.authorizationHeader())
  }

  saveHoldings(): void {
    this.http.post(this.HOLDINGS_API, this.holdings,{headers: this.getHeaders()}).subscribe(data => {
      console.log('holdings', data);
    }, this.onError);
  }

  loadHoldings(): void {
    this.http.get(this.HOLDINGS_API,{headers: this.getHeaders()}).subscribe((holdings: Holding[]) => {
      if (holdings !== null) {
        this.holdings = holdings;
        this.fetchPrices();
      }
    }, this.onError);
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

      //this.saveHoldings();

    }, err => {

      this.pricesUnavailable = true;
      if (typeof(refresher) !== 'undefined') {
        refresher.complete();
      }
    });
  }
}
