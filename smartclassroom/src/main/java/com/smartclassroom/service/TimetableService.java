package com.smartclassroom.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartclassroom.dto.TimetableRequest;
import com.smartclassroom.entity.Faculty;
import com.smartclassroom.entity.Room;
import com.smartclassroom.entity.Timetable;
import com.smartclassroom.repository.FacultyRepository;
import com.smartclassroom.repository.RoomRepository;
import com.smartclassroom.repository.TimetableRepository;

@Service
public class TimetableService {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private RoomRepository roomRepository;

    public Timetable addClass(TimetableRequest request) {

        Faculty faculty =
                facultyRepository.findById(request.getFacultyId())
                .orElseThrow();

        Room room =
                roomRepository.findById(request.getRoomId())
                .orElseThrow();

        Timetable timetable = new Timetable();

        timetable.setSubjectName(request.getSubjectName());
        timetable.setDayOfWeek(request.getDayOfWeek());
        timetable.setStartTime(request.getStartTime());
        timetable.setEndTime(request.getEndTime());
        timetable.setFaculty(faculty);
        timetable.setRoom(room);

        return timetableRepository.save(timetable);
    }

    public List<Timetable> getFacultyClasses(Integer facultyId) {

        return timetableRepository.findByFacultyId(facultyId);
    }
}