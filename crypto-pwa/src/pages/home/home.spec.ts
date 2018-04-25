import { IonicModule, NavController } from 'ionic-angular';
import { OAuthService } from 'angular-oauth2-oidc';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HomePage } from './home';
import { HoldingsProvider } from '../../providers/holdings/holdings';
import { By } from '@angular/platform-browser';

describe('HomePage', () => {
  let fixture: ComponentFixture<HomePage>;
  let component: HomePage;
  let oauthService = {
    hasValidIdToken() {
      return true;
    },
    getIdentityClaims() {}
  };
  let holdingsProvider = {
    holdings: [{crypto: 'BTC', currency: 'USD', amount: 5, value: '10000'}],
    loadHoldings() {
      return this.holdings;
    }
  };
  let loadHoldings, getIdentityClaims;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [HomePage],
      imports: [IonicModule.forRoot(HomePage)],
      providers: [NavController,
        {provide: OAuthService, useValue: oauthService},
        {provide: HoldingsProvider, useValue: holdingsProvider}
      ]
    });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HomePage);
    component = fixture.componentInstance;
    loadHoldings = jest.spyOn(holdingsProvider, 'loadHoldings');
    getIdentityClaims = jest.spyOn(oauthService, 'getIdentityClaims');
  });

  it('should be created', () => {
    expect(component).toBeDefined()
  });

  it('should call loadHoldings', () => {
    component.ionViewDidLoad();
    fixture.detectChanges();
    expect(loadHoldings).toHaveBeenCalled();
    expect(getIdentityClaims).toHaveBeenCalled();
  });

  // Test that passes when holdings is empty
  /*it('should show message with a button', () => {
    component.ionViewDidLoad();
    fixture.detectChanges();
    expect(loadHoldings).toHaveBeenCalled();
    const message: HTMLDivElement = fixture.debugElement.query(By.css('.message')).nativeElement;
    expect(message.innerHTML).toMatch(/button/);
    expect(message.innerHTML).toMatch(/Add Coins/);
  });*/

  it('should show list of currencies', () => {
    component.ionViewDidLoad();
    fixture.detectChanges();
    const list: HTMLDivElement = fixture.debugElement.query(By.css('ion-list')).nativeElement;
    expect(list.innerHTML).toMatch(/ion-item/);
    const amount = fixture.debugElement.query(By.css('.amount')).nativeElement;
    expect(amount.innerHTML).toMatch(/<strong>Coins:<\/strong> 5 <strong>Value:<\/strong> 10000/)
  });
});
