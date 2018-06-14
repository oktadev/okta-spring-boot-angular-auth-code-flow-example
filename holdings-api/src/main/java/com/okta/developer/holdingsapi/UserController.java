package com.okta.developer.holdingsapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final UserInfoRestTemplateFactory templateFactory;

    @Value("${security.oauth2.client.access-token-uri}")
    String accessTokenUri;

    public UserController(UserInfoRestTemplateFactory templateFactory) {
        this.templateFactory = templateFactory;
    }

    @GetMapping("/api/user")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> getUser(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        if (principal instanceof OAuth2Authentication) {
            OAuth2Authentication authentication = (OAuth2Authentication) principal;
            Map<String, Object> details = (Map<String, Object>) authentication.getUserAuthentication().getDetails();
            return ResponseEntity.ok().body(details);
        } else {
            return ResponseEntity.ok().body(principal.getName());
        }
    }

    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, Authentication authentication) {
        // send logout URL to client so they can initiate logout - doesn't work from the server side

        OAuth2RestTemplate oauth2RestTemplate = this.templateFactory.getUserInfoRestTemplate();
        String idToken = (String) oauth2RestTemplate.getAccessToken().getAdditionalInformation().get("id_token");

        // logout URI can be derived from accessTokenUri
        String logoutUrl = accessTokenUri.replace("token", "logout");

        Map<String, String> logoutDetails = new HashMap<>();
        logoutDetails.put("logoutUrl", logoutUrl);
        logoutDetails.put("idToken", idToken);
        request.getSession(false).invalidate();
        return ResponseEntity.ok().body(logoutDetails);
    }
}
