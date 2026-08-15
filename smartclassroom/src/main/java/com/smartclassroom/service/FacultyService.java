package com.smartclassroom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartclassroom.dto.FacultyRequest;
import com.smartclassroom.entity.Faculty;
import com.smartclassroom.entity.User;
import com.smartclassroom.repository.FacultyRepository;
import com.smartclassroom.repository.UserRepository;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
private AuditLogService auditLogService;

    public String createFaculty(FacultyRequest request) {

        User user = new User();

        user.setName(request.getFacultyName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole("FACULTY");

        User savedUser = userRepository.save(user);

        Faculty faculty = new Faculty();

        faculty.setFacultyName(request.getFacultyName());
        faculty.setDepartment(request.getDepartment());
        faculty.setEmail(request.getEmail());
        faculty.setUser(savedUser);

        facultyRepository.save(faculty);
        auditLogService.saveLog(
        "Faculty Created",
        request.getFacultyName());

        return "Faculty Created Successfully";
    }
}