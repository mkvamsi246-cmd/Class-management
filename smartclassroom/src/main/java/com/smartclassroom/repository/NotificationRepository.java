package com.smartclassroom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartclassroom.entity.Notification;

public interface NotificationRepository
        extends JpaRepository<Notification, Integer> {

    List<Notification> findByUserId(Integer userId);

}