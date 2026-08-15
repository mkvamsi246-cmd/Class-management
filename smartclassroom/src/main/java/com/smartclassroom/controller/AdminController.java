package com.smartclassroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartclassroom.dto.FacultyRequest;
import com.smartclassroom.entity.Faculty;
import com.smartclassroom.repository.FacultyRepository;
import com.smartclassroom.service.FacultyService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private FacultyService facultyService;
    @Autowired
private FacultyRepository facultyRepository;
    @GetMapping("/faculty")
public List<Faculty> getAllFaculty() {

    return facultyRepository.findAll();
}

    @PostMapping("/faculty")
    public String createFaculty(
            @RequestBody FacultyRequest request) {

        return facultyService.createFaculty(request);
    }
}