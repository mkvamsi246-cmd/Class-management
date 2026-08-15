package com.smartclassroom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartclassroom.entity.RoomRequest;

public interface RoomRequestRepository
        extends JpaRepository<RoomRequest, Integer> {

    List<RoomRequest> findByStatus(String status);

    long countByStatus(String status);

    List<RoomRequest> findByFacultyId(Integer facultyId);

    List<RoomRequest> findByRequestType(String requestType);
}