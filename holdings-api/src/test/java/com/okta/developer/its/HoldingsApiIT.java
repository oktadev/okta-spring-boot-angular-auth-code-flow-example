package com.okta.developer.its;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.okta.developer.holdingsapi.Holding;
import com.okta.developer.holdingsapi.HoldingsApiApplication;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.util.SocketUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.spotify.hamcrest.pojo.IsPojo.pojo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = HoldingsApiIT.RandomPortInitializer.class)
@SpringBootTest(classes = {HoldingsApiApplication.class},
                webEnvironment = RANDOM_PORT,
                properties = {
                    "okta.client.token=FAKE_TEST_TOKEN",
                    "okta.oauth2.localTokenValidation=false",
                    "okta.oauth2.discoveryDisabled=true",
                    "okta.client.orgUrl=http://localhost:${wiremock.server.port}",
                    "okta.oauth2.issuer=http://localhost:${wiremock.server.port}/oauth/issuer",
                    "security.oauth2.resource.userInfoUri=http://localhost:${wiremock.server.port}/oauth/userInfoUri"
                })
public class HoldingsApiIT {

    private final static String TEST_ACCESS_TOKEN = "fake-access-token";
    private final static String TEST_USER_EMAIl = "joe.coder@example.com";
    private final static String TEST_USER_ID = "user-id-123";

    private WireMockServer wireMockServer;

    @Value("${wiremock.server.port}")
    private int mockServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @PostConstruct
    private void configureRestTemplate() {

        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer "+ TEST_ACCESS_TOKEN);
            return execution.execute(request, body);
        }));

        restTemplate.getRestTemplate().setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return !response.getStatusCode().is2xxSuccessful();
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                Assert.fail("Http response failed with non-2xx status code: " + response.getStatusCode() +"\n" +
                        "body:\n" + body);
            }
        });
    }

    @Test
    public void testGetHoldings() {

        List<Holding> holdings = Arrays.asList(restTemplate.getForObject("/api/holdings", Holding[].class));

        // use Spotify's hamcrest-pojo to validate the objects
        assertThat(holdings, contains(
                pojo(Holding.class)
                    .withProperty("crypto",   is("crypto-1"))
                    .withProperty("currency", is("currency-1"))
                    .withProperty("amount",   is("amount-1")),
                pojo(Holding.class)
                    .withProperty("crypto",   is("crypto-2"))
                    .withProperty("currency", is("currency-2"))
                    .withProperty("amount",   is("amount-2"))
        ));
    }

    @Test
    public void testPostHoldings() {

        Holding[] inputHoldings = {
                new Holding()
                    .setCrypto("crypto-1")
                    .setCurrency("currency-1")
                    .setAmount("amount-1"),
                new Holding()
                    .setCrypto("crypto-2")
                    .setCurrency("currency-2")
                    .setAmount("amount-2")
        };

        List<Holding> outputHoldings = Arrays.asList(restTemplate.postForObject("/api/holdings", inputHoldings, Holding[].class));

        // output is the same as the input
        assertThat(outputHoldings, contains(
                pojo(Holding.class)
                    .withProperty("crypto",   is("crypto-1"))
                    .withProperty("currency", is("currency-1"))
                    .withProperty("amount",   is("amount-1")),
                pojo(Holding.class)
                    .withProperty("crypto",   is("crypto-2"))
                    .withProperty("currency", is("currency-2"))
                    .withProperty("amount",   is("amount-2"))
        ));

        // match a few of the encoded json values (the
        wireMockServer.verify(
                WireMock.putRequestedFor(WireMock.urlEqualTo("/api/v1/users/" + TEST_USER_ID))
                     .withRequestBody(WireMock.matchingJsonPath("$.profile.holdings", WireMock.containing("\"amount\":\"amount-1\"")))
                    .withRequestBody(WireMock.matchingJsonPath("$.profile.holdings", WireMock.containing("\"crypto\":\"crypto-2\""))));
    }

    @Before
    public void startMockServer() throws IOException {
        wireMockServer = new WireMockServer(wireMockConfig().port(mockServerPort));
        configureWireMock();
        wireMockServer.start();
    }

    @After
    public void stopMockServer() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    private void configureWireMock() throws IOException {
         // load a JSON file from the classpath
        String body = StreamUtils.copyToString(getClass().getResourceAsStream("/its/user.json"), StandardCharsets.UTF_8);

        // respond to GET for user
        wireMockServer.stubFor(WireMock.get("/api/v1/users/" + TEST_USER_EMAIl)
                .willReturn(aResponse().withBody(body)));

        // respond to PUT for user
        wireMockServer.stubFor(WireMock.put("/api/v1/users/" + TEST_USER_ID)
                .willReturn(aResponse().withBody(body)));

        // OAuth userInfoUri
        String userInfoBody = StreamUtils.copyToString(getClass().getResourceAsStream("/its/userInfo.json"), StandardCharsets.UTF_8);
        wireMockServer.stubFor(
                WireMock.get("/oauth/userInfoUri")
                    .withHeader("Authorization", WireMock.equalTo("Bearer "+ TEST_ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withBody(userInfoBody)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                ));

    }

    public static class RandomPortInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            int randomPort = SocketUtils.findAvailableTcpPort();
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
                    "wiremock.server.port=" + randomPort
            );
        }
    }
}