package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    /**
     * This method handles GET requests to /login.
     * It shows the login.html template.
     */
    @GetMapping("/login")
    public String login(Authentication authentication) {
        // If the user is already logged in, redirect them to the employee list
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/employees";
        }
        // Otherwise, show the login page
        return "login";
    }

    /**
     * This handles your "access denied" page from SecurityConfig.
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied"; // Create a template named "access-denied.html"
    }
}