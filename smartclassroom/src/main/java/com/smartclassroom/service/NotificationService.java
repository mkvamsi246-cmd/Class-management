package com.smartclassroom.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartclassroom.entity.Notification;
import com.smartclassroom.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(
            Integer userId,
            String message) {

        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(
            Integer userId) {

        return notificationRepository.findByUserId(userId);
    }
}