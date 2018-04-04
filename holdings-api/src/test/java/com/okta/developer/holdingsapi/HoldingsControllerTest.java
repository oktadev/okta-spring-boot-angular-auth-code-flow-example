package com.okta.developer.holdingsapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserProfile;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static com.spotify.hamcrest.jackson.JsonMatchers.jsonText;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.*;

import static com.spotify.hamcrest.pojo.IsPojo.pojo;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonObject;
import static com.spotify.hamcrest.jackson.IsJsonArray.jsonArray;


public class HoldingsControllerTest {

    @Test
    public void getHoldingsTest() {

        // mock the principal
        String username = "joe.coder@example.com";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);

        // mock the UserProfile
        UserProfile userProfile = mock(UserProfile.class);
        when(userProfile.get("holdings")).thenReturn(
            "[" +
                "{\"crypto\": \"crypto-value1\", \"currency\": \"currency-value1\", \"amount\": \"amount-value1\"}," +
                "{\"crypto\": \"crypto-value2\", \"currency\": \"currency-value2\", \"amount\": \"amount-value2\"}" +
            "]");

        // mock the User
        User user = mock(User.class);
        when(user.getProfile()).thenReturn(userProfile);

        // mock the Okta SDK client
        Client client = mock(Client.class);
        when(client.getUser(username)).thenReturn(user);

        // test the controller
        HoldingsController holdingsController = new HoldingsController(client);
        List<Holding> holdings = holdingsController.getHoldings(principal);

        assertThat(holdings, contains(
                pojo(Holding.class)
                    .withProperty("crypto",   is("crypto-value1"))
                    .withProperty("currency", is("currency-value1"))
                    .withProperty("amount",   is("amount-value1")),
                pojo(Holding.class)
                    .withProperty("crypto",   is("crypto-value2"))
                    .withProperty("currency", is("currency-value2"))
                    .withProperty("amount",   is("amount-value2"))
        ));
    }

    @Test
    public void saveHoldingsTest() throws IOException {

        Holding[] inputHoldings = new Holding[] {
                new Holding()
                    .setCrypto("crypto1")
                    .setCurrency("currency1")
                    .setAmount("amount1"),
                new Holding()
                    .setCrypto("crypto2")
                    .setCurrency("currency2")
                    .setAmount("amount2")
        };

        // mock the principal
        String username = "joe.coder@example.com";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);

        // mock the UserProfile
        UserProfile userProfile = mock(UserProfile.class);

        // mock the User
        User user = mock(User.class);
        when(user.getProfile()).thenReturn(userProfile);

        // mock the Okta SDK client
        Client client = mock(Client.class);
        when(client.getUser(username)).thenReturn(user);

        // test the controller
        HoldingsController holdingsController = new HoldingsController(client);
        Holding[] outputHoldings = holdingsController.saveHoldings(inputHoldings, principal);

        //////////////////////
        // Validate Results //
        //////////////////////

        // make sure the input array is the same as the output array
        assertThat(outputHoldings, is(inputHoldings));

        // verify mock interactions
        ArgumentCaptor<String> holdingsJsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(userProfile).put(eq("holdings"), holdingsJsonCaptor.capture());
        verify(user).update();

        // validate the json
        JsonNode holdingsParsed = new ObjectMapper().readTree(holdingsJsonCaptor.getValue());
        assertThat(holdingsParsed, jsonArray(contains(
                jsonObject()
                    .where("crypto", jsonText("crypto1"))
                    .where("currency", jsonText("currency1"))
                    .where("amount", jsonText("amount1")),
                jsonObject()
                    .where("crypto", jsonText("crypto2"))
                    .where("currency", jsonText("currency2"))
                    .where("amount", jsonText("amount2"))
        )));
    }
}