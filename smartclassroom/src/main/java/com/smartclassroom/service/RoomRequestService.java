package com.smartclassroom.service;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
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
}