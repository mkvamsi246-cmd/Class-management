package com.smartclassroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartclassroom.entity.Room;
import com.smartclassroom.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping
    public Room addRoom(@RequestBody Room room) {
        return roomService.addRoom(room);
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @PutMapping("/{id}")
    public Room updateRoom(
            @PathVariable Integer id,
            @RequestBody Room room) {

        return roomService.updateRoom(id, room);
    }
    @GetMapping("/available")
public List<Room> availableRooms() {

    return roomService.getAvailableRooms();

}

    @DeleteMapping("/{id}")
    public String deleteRoom(
            @PathVariable Integer id) {

        roomService.deleteRoom(id);

        return "Room Deleted Successfully";
    }
}