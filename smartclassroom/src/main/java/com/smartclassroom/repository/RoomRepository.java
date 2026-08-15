package com.smartclassroom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartclassroom.entity.Room;

public interface RoomRepository
        extends JpaRepository<Room,Integer> {

    List<Room> findByStatus(String status);

    long countByStatus(String status);

    Optional<Room> findByRoomNumber(String roomNumber);
}