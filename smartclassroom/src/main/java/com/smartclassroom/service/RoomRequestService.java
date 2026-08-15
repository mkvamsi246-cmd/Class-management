package com.smartclassroom.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartclassroom.dto.RoomRequestDTO;
import com.smartclassroom.entity.Faculty;
import com.smartclassroom.entity.Room;
import com.smartclassroom.entity.RoomRequest;
import com.smartclassroom.entity.Timetable;
import com.smartclassroom.repository.FacultyRepository;
import com.smartclassroom.repository.RoomRepository;
import com.smartclassroom.repository.RoomRequestRepository;
import com.smartclassroom.repository.TimetableRepository;

@Service
public class RoomRequestService {

    @Autowired private RoomRequestRepository roomRequestRepository;
    @Autowired private FacultyRepository facultyRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private TimetableRepository timetableRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private AuditLogService auditLogService;

    // ──────────────────────────────────────────────────────────────────────
    // CREATE
    // ──────────────────────────────────────────────────────────────────────
    public RoomRequest createRequest(RoomRequestDTO dto) {

        Faculty requesterFaculty = facultyRepository
                .findById(dto.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Faculty Not Found"));

        String type = dto.getRequestType() != null ? dto.getRequestType() : "UNOCCUPIED";

        RoomRequest request = new RoomRequest();
        request.setFaculty(requesterFaculty);
        request.setReason(dto.getReason());
        request.setStatus("PENDING");
        request.setRequestType(type);
        request.setCreatedAt(LocalDateTime.now());

        if ("SWAP".equals(type)) {
            // ── SWAP request ────────────────────────────────────────────
            Timetable requesterSlot = timetableRepository
                    .findById(dto.getRequesterTimetableId())
                    .orElseThrow(() -> new RuntimeException("Requester Timetable Not Found"));

            Timetable targetSlot = timetableRepository
                    .findById(dto.getTargetTimetableId())
                    .orElseThrow(() -> new RuntimeException("Target Timetable Not Found"));

            request.setRequesterTimetable(requesterSlot);
            request.setTargetTimetable(targetSlot);

            RoomRequest saved = roomRequestRepository.save(request);

            // ── Notify TARGET faculty immediately ─────────────────────
            Faculty targetFaculty = targetSlot.getFaculty();
            if (targetFaculty != null && targetFaculty.getUser() != null) {
                String msg = String.format(
                        "%s wants to swap their '%s' class (%s, %s–%s) with your '%s' class (%s, %s–%s). Awaiting admin approval.",
                        requesterFaculty.getFacultyName(),
                        requesterSlot.getSubjectName(),
                        requesterSlot.getDayOfWeek(),
                        requesterSlot.getStartTime(),
                        requesterSlot.getEndTime(),
                        targetSlot.getSubjectName(),
                        targetSlot.getDayOfWeek(),
                        targetSlot.getStartTime(),
                        targetSlot.getEndTime());
                notificationService.createNotification(targetFaculty.getUser().getId(), msg);
            }

            auditLogService.saveLog("Swap Request Created", requesterFaculty.getFacultyName());
            return saved;

        } else {
            // ── UNOCCUPIED request ───────────────────────────────────────
            Room room = roomRepository
                    .findById(dto.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room Not Found"));

            String dayOfWeek = dto.getRequestDate()
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                    .toUpperCase();

            List<Timetable> conflicts = timetableRepository
                    .findByRoomIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
                            room.getId(), dayOfWeek, dto.getEndTime(), dto.getStartTime());

            if (!conflicts.isEmpty()) {
                throw new RuntimeException("Room is already booked during this time slot");
            }

            request.setRoom(room);
            request.setRequestDate(dto.getRequestDate());
            request.setStartTime(dto.getStartTime());
            request.setEndTime(dto.getEndTime());

            return roomRequestRepository.save(request);
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // READ
    // ──────────────────────────────────────────────────────────────────────
    public List<RoomRequest> getAllRequests() {
        return roomRequestRepository.findAll();
    }

    public List<RoomRequest> getFacultyRequests(Integer facultyId) {
        return roomRequestRepository.findByFacultyId(facultyId);
    }

    // ──────────────────────────────────────────────────────────────────────
    // APPROVE
    // ──────────────────────────────────────────────────────────────────────
    @Transactional
    public RoomRequest approveRequest(Integer id) {

        RoomRequest request = roomRequestRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Request Not Found"));

        request.setStatus("APPROVED");

        if ("SWAP".equals(request.getRequestType())) {
            Timetable requesterSlot = request.getRequesterTimetable();
            Timetable targetSlot   = request.getTargetTimetable();

            Faculty requesterFaculty = requesterSlot.getFaculty();
            Faculty targetFaculty    = targetSlot.getFaculty();

            // Atomically swap faculty on both timetable rows
            requesterSlot.setFaculty(targetFaculty);
            targetSlot.setFaculty(requesterFaculty);
            timetableRepository.save(requesterSlot);
            timetableRepository.save(targetSlot);

            // Notify requester
            if (requesterFaculty.getUser() != null) {
                notificationService.createNotification(
                        requesterFaculty.getUser().getId(),
                        String.format(
                                "✅ Your swap request was APPROVED. You are now assigned to '%s' (%s, %s–%s).",
                                targetSlot.getSubjectName(),
                                targetSlot.getDayOfWeek(),
                                targetSlot.getStartTime(),
                                targetSlot.getEndTime()));
            }
            // Notify target faculty
            if (targetFaculty.getUser() != null) {
                notificationService.createNotification(
                        targetFaculty.getUser().getId(),
                        String.format(
                                "🔄 Admin approved a class swap initiated by %s. You are now assigned to '%s' (%s, %s–%s).",
                                request.getFaculty().getFacultyName(),
                                requesterSlot.getSubjectName(),
                                requesterSlot.getDayOfWeek(),
                                requesterSlot.getStartTime(),
                                requesterSlot.getEndTime()));
            }

        } else {
            // UNOCCUPIED
            if (request.getFaculty().getUser() != null) {
                notificationService.createNotification(
                        request.getFaculty().getUser().getId(),
                        "✅ Your room request has been approved.");
            }
        }

        RoomRequest updated = roomRequestRepository.save(request);
        auditLogService.saveLog("Room Request Approved", request.getFaculty().getFacultyName());
        return updated;
    }

    // ──────────────────────────────────────────────────────────────────────
    // REJECT
    // ──────────────────────────────────────────────────────────────────────
    @Transactional
    public RoomRequest rejectRequest(Integer id) {

        RoomRequest request = roomRequestRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Request Not Found"));

        request.setStatus("REJECTED");
        RoomRequest updated = roomRequestRepository.save(request);

        if ("SWAP".equals(request.getRequestType())) {
            // Notify requester
            if (request.getFaculty().getUser() != null) {
                notificationService.createNotification(
                        request.getFaculty().getUser().getId(),
                        "❌ Your swap request has been REJECTED by the admin.");
            }
            // Notify target faculty
            Faculty targetFaculty = request.getTargetTimetable().getFaculty();
            if (targetFaculty != null && targetFaculty.getUser() != null) {
                notificationService.createNotification(
                        targetFaculty.getUser().getId(),
                        String.format(
                                "❌ The swap request from %s targeting your class has been REJECTED by the admin.",
                                request.getFaculty().getFacultyName()));
            }
        } else {
            if (request.getFaculty().getUser() != null) {
                notificationService.createNotification(
                        request.getFaculty().getUser().getId(),
                        "❌ Your room request has been rejected.");
            }
        }

        auditLogService.saveLog("Room Request Rejected", request.getFaculty().getFacultyName());
        return updated;
    }

    // ──────────────────────────────────────────────────────────────────────
    // AUTO ASSIGNMENT (AFTER 2 MINUTES TIMEOUT WITH PRIORITY RESOLUTION)
    // ──────────────────────────────────────────────────────────────────────
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void autoAssignPendingRequests() {
        List<RoomRequest> pendingRequests = roomRequestRepository.findByStatus("PENDING");
        if (pendingRequests.isEmpty()) {
            return;
        }

        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(2);
        List<RoomRequest> expiredRequests = pendingRequests.stream()
                .filter(r -> r.getCreatedAt() != null && !r.getCreatedAt().isAfter(cutoffTime))
                .collect(Collectors.toList());

        if (expiredRequests.isEmpty()) {
            return;
        }

        // Sort expired requests by Faculty Priority (descending) then submission time (ascending)
        expiredRequests.sort((r1, r2) -> {
            int p1 = (r1.getFaculty() != null && r1.getFaculty().getPriority() != null) ? r1.getFaculty().getPriority() : 1;
            int p2 = (r2.getFaculty() != null && r2.getFaculty().getPriority() != null) ? r2.getFaculty().getPriority() : 1;
            if (p1 != p2) {
                return Integer.compare(p2, p1); // Higher priority first
            }
            if (r1.getCreatedAt() != null && r2.getCreatedAt() != null) {
                return r1.getCreatedAt().compareTo(r2.getCreatedAt()); // Earlier timestamp first
            }
            return 0;
        });

        for (RoomRequest request : expiredRequests) {
            RoomRequest current = roomRequestRepository.findById(request.getId()).orElse(null);
            if (current == null || !"PENDING".equalsIgnoreCase(current.getStatus())) {
                continue;
            }

            if ("SWAP".equals(current.getRequestType())) {
                Timetable requesterSlot = current.getRequesterTimetable();
                Timetable targetSlot = current.getTargetTimetable();

                if (requesterSlot == null || targetSlot == null) {
                    current.setStatus("REJECTED");
                    roomRequestRepository.save(current);
                    continue;
                }

                // Approve request & swap slots
                approveRequest(current.getId());
                auditLogService.saveLog(
                        "Swap Request Auto-Approved (2m Timeout, Priority: " + getFacultyPriority(current.getFaculty()) + ")",
                        current.getFaculty() != null ? current.getFaculty().getFacultyName() : "System");

                if (current.getFaculty() != null && current.getFaculty().getUser() != null) {
                    notificationService.createNotification(
                            current.getFaculty().getUser().getId(),
                            "⚡ Your swap request was automatically approved and assigned after 2 minutes (Faculty Priority: "
                                    + getFacultyPriority(current.getFaculty()) + ").");
                }
            } else {
                // UNOCCUPIED request
                Room room = current.getRoom();
                boolean isOccupied = isRoomOccupiedOrAlreadyApproved(
                        room, current.getRequestDate(), current.getStartTime(), current.getEndTime(), current.getId());

                if (isOccupied) {
                    current.setStatus("REJECTED");
                    roomRequestRepository.save(current);

                    if (current.getFaculty() != null && current.getFaculty().getUser() != null) {
                        notificationService.createNotification(
                                current.getFaculty().getUser().getId(),
                                "❌ Your room request for " + (room != null ? room.getRoomNumber() : "room")
                                        + " was REJECTED because the slot was allocated to a higher priority request.");
                    }
                    auditLogService.saveLog(
                            "Room Request Auto-Rejected due to Conflict",
                            current.getFaculty() != null ? current.getFaculty().getFacultyName() : "System");
                } else {
                    approveRequest(current.getId());
                    auditLogService.saveLog(
                            "Room Request Auto-Approved (2m Timeout, Priority: " + getFacultyPriority(current.getFaculty()) + ")",
                            current.getFaculty() != null ? current.getFaculty().getFacultyName() : "System");

                    if (current.getFaculty() != null && current.getFaculty().getUser() != null) {
                        notificationService.createNotification(
                                current.getFaculty().getUser().getId(),
                                "⚡ Your room request for " + (room != null ? room.getRoomNumber() : "room")
                                        + " was automatically approved and assigned after 2 minutes (Faculty Priority: "
                                        + getFacultyPriority(current.getFaculty()) + ").");
                    }
                }
            }
        }
    }

    private int getFacultyPriority(Faculty faculty) {
        return (faculty != null && faculty.getPriority() != null) ? faculty.getPriority() : 1;
    }

    private boolean isRoomOccupiedOrAlreadyApproved(Room room, LocalDate requestDate, LocalTime startTime, LocalTime endTime, Integer excludeRequestId) {
        if (room == null || requestDate == null || startTime == null || endTime == null) {
            return false;
        }
        String dayOfWeek = requestDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
        List<Timetable> timetableConflicts = timetableRepository
                .findByRoomIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
                        room.getId(), dayOfWeek, endTime, startTime);
        if (!timetableConflicts.isEmpty()) {
            return true;
        }

        List<RoomRequest> approvedRoomRequests = roomRequestRepository.findByStatus("APPROVED");
        for (RoomRequest approved : approvedRoomRequests) {
            if (excludeRequestId != null && excludeRequestId.equals(approved.getId())) {
                continue;
            }
            if ("UNOCCUPIED".equals(approved.getRequestType()) && approved.getRoom() != null
                    && approved.getRoom().getId().equals(room.getId())
                    && requestDate.equals(approved.getRequestDate())) {
                if (startTime.isBefore(approved.getEndTime()) && endTime.isAfter(approved.getStartTime())) {
                    return true;
                }
            }
        }
        return false;
    }
}