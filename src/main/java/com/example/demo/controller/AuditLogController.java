package com.example.demo.controller;

import com.example.demo.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/audit-logs")
    public String viewAuditLogs(Model model) {
        model.addAttribute("logs", auditLogService.getAllLogs());
        return "audit-logs";
    }
}