package com.smartclassroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartclassroom.entity.AuditLog;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Integer> {

}