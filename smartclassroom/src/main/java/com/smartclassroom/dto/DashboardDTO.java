package com.smartclassroom.dto;

public class DashboardDTO {

    private Long totalRooms;
    private Long availableRooms;
    private Long occupiedRooms;
    private Long pendingRequests;
    private Long totalFaculty;
    private Long totalClasses;

    public DashboardDTO() {
    }

    public Long getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Long totalRooms) {
        this.totalRooms = totalRooms;
    }

    public Long getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(Long availableRooms) {
        this.availableRooms = availableRooms;
    }

    public Long getOccupiedRooms() {
        return occupiedRooms;
    }

    public void setOccupiedRooms(Long occupiedRooms) {
        this.occupiedRooms = occupiedRooms;
    }

    public Long getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(Long pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public Long getTotalFaculty() {
        return totalFaculty;
    }

    public void setTotalFaculty(Long totalFaculty) {
        this.totalFaculty = totalFaculty;
    }

    public Long getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(Long totalClasses) {
        this.totalClasses = totalClasses;
    }
}