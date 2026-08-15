package com.smartclassroom.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "room_requests")
public class RoomRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // UNOCCUPIED or SWAP
    private String requestType;

    private LocalDate requestDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String reason;

    private String status;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // SWAP: the requester's own timetable slot
    @ManyToOne
    @JoinColumn(name = "requester_timetable_id")
    private Timetable requesterTimetable;

    // SWAP: the target timetable slot to swap with
    @ManyToOne
    @JoinColumn(name = "target_timetable_id")
    private Timetable targetTimetable;

    public RoomRequest() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Timetable getRequesterTimetable() {
        return requesterTimetable;
    }

    public void setRequesterTimetable(Timetable requesterTimetable) {
        this.requesterTimetable = requesterTimetable;
    }

    public Timetable getTargetTimetable() {
        return targetTimetable;
    }

    public void setTargetTimetable(Timetable targetTimetable) {
        this.targetTimetable = targetTimetable;
    }
}