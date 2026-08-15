package com.smartclassroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartclassroom.dto.TimetableRequest;
import com.smartclassroom.entity.Timetable;
import com.smartclassroom.repository.TimetableRepository;
import com.smartclassroom.service.TimetableService;

@RestController
@RequestMapping("/api/timetable")
@CrossOrigin(origins = "*")
public class TimetableController {

    @Autowired
    private TimetableService timetableService;

    @Autowired
    private TimetableRepository timetableRepository;

    @GetMapping
    public List<Timetable> getAllTimetable() {
        return timetableRepository.findAll();
    }

    @PostMapping
    public Timetable addClass(
            @RequestBody TimetableRequest request) {
        return timetableService.addClass(request);
    }

    @GetMapping("/faculty/{facultyId}")
    public List<Timetable> getFacultyClasses(
            @PathVariable Integer facultyId) {
        return timetableService.getFacultyClasses(facultyId);
    }

    @DeleteMapping("/{id}")
    public String deleteTimetableEntry(
            @PathVariable Integer id) {
        timetableRepository.deleteById(id);
        return "Timetable entry deleted";
    }
}