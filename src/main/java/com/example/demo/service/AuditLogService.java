package com.example.demo.service;

import com.example.demo.model.AuditLog;
import com.example.demo.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    // REQUIRES_NEW ensures logging succeeds even if the main transaction rolls back (optional choice, usually good for audits)
    // However, for strict consistency (only log if success), use REQUIRED (default).
    // We will use default to ensure we don't log "Employee Created" if the DB save actually failed.
    @Transactional
    public void saveLog(String action, String username, String details) {
        AuditLog log = new AuditLog(action, username, details);
        auditLogRepository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }
}