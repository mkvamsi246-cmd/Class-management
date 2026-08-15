package com.smartclassroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartclassroom.entity.Notification;
import com.smartclassroom.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{userId}")
    public List<Notification> getNotifications(
            @PathVariable Integer userId) {

        return notificationService
                .getUserNotifications(userId);
    }
}