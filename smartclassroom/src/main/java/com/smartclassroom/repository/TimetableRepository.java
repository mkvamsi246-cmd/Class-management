package com.smartclassroom.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartclassroom.entity.Timetable;

public interface TimetableRepository
        extends JpaRepository<Timetable, Integer> {

    List<Timetable> findByFacultyId(Integer facultyId);

    // Conflict check: overlapping booking for a room on a given day
    List<Timetable> findByRoomIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
            Integer roomId,
            String dayOfWeek,
            LocalTime endTime,
            LocalTime startTime);
}