import { getTestBed, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HoldingsProvider } from './holdings';
import { OAuthService } from 'angular-oauth2-oidc';

describe('HoldingsProvider', () => {
  let injector: TestBed;
  let provider: HoldingsProvider;
  let httpMock: HttpTestingController;
  let oauthService = {
    hasValidIdToken() {
      return true;
    },
    authorizationHeader() {
      return "random-string";
    }
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [HoldingsProvider,
        {provide: OAuthService, useValue: oauthService}
      ]
    });

    injector = getTestBed();
    provider = injector.get(HoldingsProvider);
    httpMock = injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify()
  });

  it('should be created', () => {
    expect(provider).toBeTruthy();
  });

  it('should retrieve holdings', () => {
    const fakeHoldings = [
      {crypto: 'BTC', currency: 'USD', amount: 5, value: '10000'},
      {crypto: 'ETH', currency: 'USD', amount: 100, value: '700'}
    ];

    provider.loadHoldings();
    const req = httpMock.expectOne(provider.HOLDINGS_API);
    expect(req.request.method).toBe('GET');
    req.flush(fakeHoldings);

    expect(provider.holdings.length).toBe(2);
    expect(provider.holdings[0].crypto).toBe('BTC');
    expect(provider.holdings).toEqual(fakeHoldings);

    // calls to get prices
    httpMock.expectOne('https://api.cryptonator.com/api/ticker/BTC-USD');
    httpMock.expectOne('https://api.cryptonator.com/api/ticker/ETH-USD');
  });
});
