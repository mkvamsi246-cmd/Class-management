package com.smartclassroom.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartclassroom.entity.Room;
import com.smartclassroom.repository.RoomRepository;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AuditLogService auditLogService;

    public Room addRoom(Room room) {
        // Auto-set status to AVAILABLE on creation
        if (room.getStatus() == null || room.getStatus().isBlank()) {
            room.setStatus("AVAILABLE");
        }
        Room savedRoom = roomRepository.save(room);
        auditLogService.saveLog("Room Created", room.getRoomNumber());
        return savedRoom;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room updateRoom(Integer id, Room room) {
        Room existingRoom = roomRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Room Not Found"));

        existingRoom.setRoomNumber(room.getRoomNumber());
        existingRoom.setRoomType(room.getRoomType());
        // Keep status update-able but only these three fields matter
        if (room.getStatus() != null) {
            existingRoom.setStatus(room.getStatus());
        }

        return roomRepository.save(existingRoom);
    }

    public void deleteRoom(Integer id) {
        roomRepository.deleteById(id);
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus("AVAILABLE");
    }
}