package com.smartclassroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartclassroom.dto.RoomRequestDTO;
import com.smartclassroom.entity.RoomRequest;
import com.smartclassroom.service.RoomRequestService;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "http://localhost:4200")
public class RoomRequestController {

    @Autowired
    private RoomRequestService roomRequestService;

    @PostMapping
    public RoomRequest createRequest(
            @RequestBody RoomRequestDTO dto) {

        return roomRequestService.createRequest(dto);
    }

    @GetMapping
    public List<RoomRequest> getAllRequests() {

        return roomRequestService.getAllRequests();
    }

    @PutMapping("/{id}/approve")
    public RoomRequest approveRequest(
            @PathVariable Integer id) {

        return roomRequestService.approveRequest(id);
    }

    @PutMapping("/{id}/reject")
    public RoomRequest rejectRequest(
            @PathVariable Integer id) {

        return roomRequestService.rejectRequest(id);
    }

    @GetMapping("/faculty/{facultyId}")
    public List<RoomRequest> getFacultyRequests(
            @PathVariable Integer facultyId) {
        return roomRequestService.getFacultyRequests(facultyId);
    }
    
}