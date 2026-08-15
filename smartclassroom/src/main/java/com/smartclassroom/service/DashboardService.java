package com.smartclassroom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartclassroom.dto.DashboardDTO;
import com.smartclassroom.repository.FacultyRepository;
import com.smartclassroom.repository.RoomRepository;
import com.smartclassroom.repository.RoomRequestRepository;
import com.smartclassroom.repository.TimetableRepository;

@Service
public class DashboardService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private RoomRequestRepository roomRequestRepository;

    @Autowired
    private TimetableRepository timetableRepository;

    public DashboardDTO getDashboardData() {

        DashboardDTO dto = new DashboardDTO();

        dto.setTotalRooms(
                roomRepository.count());

        dto.setAvailableRooms(
                roomRepository.countByStatus("AVAILABLE"));

        dto.setOccupiedRooms(
                roomRepository.countByStatus("OCCUPIED"));

        dto.setPendingRequests(
                roomRequestRepository.countByStatus("PENDING"));

        dto.setTotalFaculty(
                facultyRepository.count());

        dto.setTotalClasses(
                timetableRepository.count());

        return dto;
    }
}