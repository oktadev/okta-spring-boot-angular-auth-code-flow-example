package com.okta.developer.holdingsapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/api/user")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> getUser(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>("User not authenticated", HttpStatus.NOT_FOUND);
        }
        OAuth2Authentication authentication = (OAuth2Authentication) principal;
        // todo: figure out how to get user's info, the following works in JHipster
        // maybe I need a custom security config?
        Map<String, Object> details = (Map<String, Object>) authentication.getUserAuthentication().getDetails();
        return ResponseEntity.ok().body(details);
    }
}
