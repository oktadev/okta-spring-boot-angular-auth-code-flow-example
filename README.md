# Cryptocurrency PWA with Holdings Data in Okta
 
This example app shows how to use Okta's Java SDK to store cryptocurrency holdings as a custom user attribute.

Please read [Use Okta (Instead of Local Storage) to Store Your User's Data Securely](https://developer.okta.com/blog/2018/01/23/replace-local-storage-with-okta-profile-attributes) to see how this app was created.

**Prerequisites:** [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) and [Node.js](https://nodejs.org/).

> [Okta](https://developer.okta.com/) has Authentication and User Management APIs that reduce development time with instant-on, scalable user infrastructure. Okta's intuitive API and expert support make it easy for developers to authenticate, manage, and secure users and roles in any application.

* [Getting Started](#getting-started)
* [Links](#links)
* [Help](#help)
* [License](#license)

## Getting Started

To install this example application, run the following commands:

```bash
git clone git@github.com:oktadeveloper/okta-ionic-crypto-java-sdk.git
cd okta-ionic-crypto-java-sdk
```

This will get a copy of the project installed locally. To install all of its dependencies and start each app, follow the instructions below.

To run the server, cd into the `holdings-api` folder and run:
 
```bash
./mvnw spring-boot:run
```

To run the client, cd into the `crypto-pwa` folder and run:
 
```bash
npm install -g ionic
npm i && ionic serve
```

### Setup Okta

The first thing you’ll need to do is add a `holdings` attribute to your organization’s user profiles. Log in to the Okta Developer Console, then navigate to **Users** > **Profile Editor**. Click on **Profile** for the first profile in the table. You can identify it by its Okta logo. Click **Add Attribute** and use the following values:

* Display name: `Holdings`
* Variable name: `holdings`
* Description: `Cryptocurrency Holdings`

You will need to [create an API Token and OIDC App](https://developer.okta.com/blog/2018/01/23/replace-local-storage-with-okta-profile-attributes#create-an-api-token) to get your values to perform authentication. 

Log in to your Okta Developer account (or [sign up](https://developer.okta.com/signup/) if you don’t have an account) and navigate to **Applications** > **Add Application**. Click **Single-Page App**, click **Next**, and give the app a name you’ll remember. Click **Done**.

For the Okta Java SDK to talk to Okta’s API, you’ll need to create an API token. The abbreviated steps are as follows:

1. Log in to your Developer Console
2. Navigate to **API** > **Tokens** and click **Create Token**
3. Give your token a name, then copy its value

#### Server Configuration

Open `holdings-api/src/main/resources/application.properties` and add your API token as a property. While you're there, set the `issuer` and `clientId` to match your OIDC application.

**NOTE:** The value of `{yourOktaDomain}` should be something like `dev-123456.oktapreview.com`. Make sure you don't include `-admin` in the value!

```properties
okta.oauth2.issuer=https://{yourOktaDomain}.com/oauth2/default
okta.oauth2.clientId={yourClientId}
okta.client.token=XXX
```

#### Client Configuration

For the client, set the `issuer` and copy the `clientId` into `src/pages/login/login.ts`.

```typescript
const config = {
  issuer: 'https://{yourOktaDomain}.com/oauth2/default',
  redirectUri: window.location.origin + '/implicit/callback',
  clientId: '{clientId}'
};
```

## Links

This example uses the following libraries provided by Okta:

* [Okta Spring Boot Starter](https://github.com/okta/okta-spring-boot)
* [Okta Auth SDK](https://github.com/okta/okta-auth-js)

## Help

Please post any questions as comments on the [blog post](https://developer.okta.com/blog/2018/01/23/replace-local-storage-with-okta-profile-attributes), or visit our [Okta Developer Forums](https://devforum.okta.com/). You can also email developers@okta.com if would like to create a support ticket.

## License

Apache 2.0, see [LICENSE](LICENSE).
