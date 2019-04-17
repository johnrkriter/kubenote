package com.example.sba.keycloak;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty("keycloak.enabled")
public class KeycloakController {

    /**
     * Propagates the logout to the Keycloak infrastructure
     */
    @PostMapping("/admin/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/admin";
    }
}