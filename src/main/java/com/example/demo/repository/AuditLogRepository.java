package com.example.demo.repository;

import com.example.demo.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // Find logs ordered by latest first
    List<AuditLog> findAllByOrderByTimestampDesc();
}