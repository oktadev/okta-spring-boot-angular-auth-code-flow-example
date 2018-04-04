package com.okta.developer.its;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.okta.developer.holdingsapi.HoldingsApiApplication;
import com.okta.developer.holdingsapi.HoldingsController;
import com.okta.sdk.client.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.SocketUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = HoldingsApiIT.RandomPortInitializer.class)
@SpringBootTest(classes = {HoldingsApiApplication.class},
                webEnvironment = RANDOM_PORT,
                properties = {
                    "okta.client.token=FAKE_TEST_TOKEN"})
public class HoldingsApiIT {

    private WireMockServer wireMockServer;

    @Value("${wiremock.server.port}")
    private int mockServerPort;

    @Autowired
    private HoldingsController holdingsController;

    @Autowired
    private Client client;

    @Test
    @WithMockUser(username="user-id-123")
    public void foo() throws Exception {

        SecurityContext context = SecurityContextHolder.getContext();

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(holdingsController).build();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/holdings")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("user-id-123"))
                .with(securityContext(context)))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount").value("amount-1"));

//        Holding[] result = restTemplate.getForObject("/api/holdings", Holding[].class);


    }

    @Before
    public void startMockServer() {
        wireMockServer = new WireMockServer(wireMockConfig().port(mockServerPort));
        configureWireMock(wireMockServer);
        wireMockServer.start();
    }

    private void configureWireMock(WireMockServer wireMockServer) {

        String userId = "user-id-123";
        wireMockServer.stubFor(get("/api/v1/users/" + userId)
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody("{id='user-id-123'}")));
    }

    @After
    public void stopMockServer() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    public static class RandomPortInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            int randomPort = SocketUtils.findAvailableTcpPort();
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
                    "wiremock.server.port=" + randomPort,
                    "okta.client.orgUrl=https://localhost:" + randomPort);
        }
    }
}