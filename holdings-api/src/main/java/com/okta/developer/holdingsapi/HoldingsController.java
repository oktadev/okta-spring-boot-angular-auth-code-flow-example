package com.okta.developer.holdingsapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.user.User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/holdings")
public class HoldingsController {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Client client;
    private final String HOLDINGS_ATTRIBUTE_NAME = "holdings";

    public HoldingsController(Client client) {
        this.client = client;
    }

    @GetMapping
    public List<Holding> getHoldings(Principal principal) {
        User user = client.getUser(principal.getName());

        String holdingsFromOkta = (String) user.getProfile().get(HOLDINGS_ATTRIBUTE_NAME);
        List<Holding> holdings = new LinkedList<>();

        if (holdingsFromOkta != null) {
            try {
                holdings = mapper.readValue(holdingsFromOkta, new TypeReference<List<Holding>>() {
                });
            } catch (IOException io) {
                System.err.println(("Error marshalling Okta custom data: " + io.getMessage()));
                io.printStackTrace();
            }
        }

        return holdings;
    }

    @PostMapping
    public Holding[] saveHoldings(@RequestBody Holding[] holdings, Principal principal) {
        User user = client.getUser(principal.getName());
        try {
            String json = mapper.writeValueAsString(holdings);
            user.getProfile().put(HOLDINGS_ATTRIBUTE_NAME, json);
            user.update();
        } catch (JsonProcessingException e) {
            System.err.println(("Error saving Okta custom data: " + e.getMessage()));
            e.printStackTrace();
        }
        return holdings;
    }
}
