# cryptoPWA with Okta Authentication

This example shows to create an cryptocurrency application in Ionic that uses Okta for authentication. The cryptoPWA app used in this example was originally by [Josh Morony](https://twitter.com/joshuamorony) in [Building a Cryptocurrency Price Tracker PWA in Ionic](https://www.joshmorony.com/building-a-cryptocurrency-price-tracker-pwa-in-ionic/).

Please read [Protect your Cryptocurrency Wealth Tracking PWA with Okta]() to see how this application was created.

**Prerequisites:** [Node.js](https://nodejs.org/).

> [Okta](https://developer.okta.com/) has Authentication and User Management APIs that reduce development time with instant-on, scalable user infrastructure. Okta's intuitive API and expert support make it easy for developers to authenticate, manage, and secure users and roles in any application.

* [Getting Started](#getting-started)
* [Links](#links)
* [Help](#help)
* [License](#license)

## Getting Started

To install this example application, run the following commands:

```bash
git clone git@github.com:oktadeveloper/okta-ionic-crypto-pwa.git
cd okta-ionic-crypto-pwa
```

This will get a copy of the project installed locally. Then run the following command to install Ionic and Cordova.

```
npm install -g cordova ionic
```

Then run the application:

```
npm install
ionic serve
```

To integrate Okta's Identity Platform for user authentication, you'll first need to:

* [Register](https://www.okta.com/developer/signup/) and create an OIDC application
* Log in to your Okta account and navigate to **Applications > Add Application** 
* Select **SPA** and click **Next**
* Give your application a name (e.g. "Crypto PWA")
* Change the **Base URI** and **Login redirect URI** to `http://localhost:8100` and click **Done**. 

After performing these steps, copy the `clientId` into `src/pages/login/login.ts` and change `{yourOktaDomain}` to match your account's id.

```typescript
constructor(private navCtrl: NavController, private oauthService: OAuthService) {
  oauthService.redirectUri = 'http://localhost:8100';
  oauthService.clientId = '{clientId}';
  oauthService.scope = 'openid profile email';
  oauthService.issuer = 'https://{youtOktaDomain}.com/oauth2/default';
  oauthService.tokenValidationHandler = new JwksValidationHandler();
  ...
}
```

Your OIDC app should have settings like the following:

<img src="https://developer.okta.com/assets/blog/ionic-authentication/oidc-settings-46747e5e9af164cf56d05f055a659520252558872d9319cadd831d5e7104b990.png" width="700" alt="Okta OIDC Settings"/>

## Links

This example uses the following libraries provided by Okta:

* [Okta Auth SDK](https://github.com/okta/okta-auth-js)

It also uses the following library provided by [Manfred Steyer](https://github.com/manfredsteyer):

* [angular-oauth2-oidc](https://github.com/manfredsteyer/angular-oauth2-oidc)

## Help

Please post any questions as comments on the [blog post](), or visit our [Okta Developer Forums](https://devforum.okta.com/). You can also email developers@okta.com if would like to create a support ticket.

## License

Apache 2.0, see [LICENSE](LICENSE).
