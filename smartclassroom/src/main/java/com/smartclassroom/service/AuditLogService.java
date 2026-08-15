package com.smartclassroom.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartclassroom.entity.AuditLog;
import com.smartclassroom.repository.AuditLogRepository;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void saveLog(
            String action,
            String username) {

        AuditLog log = new AuditLog();

        log.setAction(action);
        log.setUsername(username);
        log.setCreatedAt(LocalDateTime.now());

        auditLogRepository.save(log);
    }
}