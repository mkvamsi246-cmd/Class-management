package com.smartclassroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartclassroom.entity.AuditLog;
import com.smartclassroom.repository.AuditLogRepository;

@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "http://localhost:4200")
public class AuditController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping
    public List<AuditLog> getAllLogs() {

        return auditLogRepository.findAll();
    }
}