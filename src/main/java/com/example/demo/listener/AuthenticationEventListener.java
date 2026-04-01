package com.example.demo.listener;

import com.example.demo.service.AuditLogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventListener {

    private static final Logger logger = LogManager.getLogger(AuthenticationEventListener.class);

    @Autowired
    private AuditLogService auditLogService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        logger.info("Audit: Action=LOGIN_SUCCESS, User={}", username);
        auditLogService.saveLog("LOGIN", username, "User logged in successfully");
    }

    @EventListener
    public void onLogout(LogoutSuccessEvent event) {
        String username = event.getAuthentication().getName();
        if (username == null) username = "Unknown"; // Handle cases where session is already gone

        logger.info("Audit: Action=LOGOUT_SUCCESS, User={}", username);
        auditLogService.saveLog("LOGOUT", username, "User logged out");
    }
}