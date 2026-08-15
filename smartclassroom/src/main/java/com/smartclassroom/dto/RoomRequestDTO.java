package com.smartclassroom.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class RoomRequestDTO {

    private Integer facultyId;

    private Integer roomId;

    private LocalDate requestDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String reason;

    // UNOCCUPIED or SWAP
    private String requestType;

    // SWAP only: the requester's own timetable entry id
    private Integer requesterTimetableId;

    // SWAP only: the target timetable entry id to swap with
    private Integer targetTimetableId;

    public RoomRequestDTO() {
    }

    public Integer getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Integer facultyId) {
        this.facultyId = facultyId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
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

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Integer getRequesterTimetableId() {
        return requesterTimetableId;
    }

    public void setRequesterTimetableId(Integer requesterTimetableId) {
        this.requesterTimetableId = requesterTimetableId;
    }

    public Integer getTargetTimetableId() {
        return targetTimetableId;
    }

    public void setTargetTimetableId(Integer targetTimetableId) {
        this.targetTimetableId = targetTimetableId;
    }
}