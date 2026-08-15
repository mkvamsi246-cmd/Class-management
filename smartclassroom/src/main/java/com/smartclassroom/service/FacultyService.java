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
        String email = request.getEmail().trim();

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setRole("FACULTY");
            return newUser;
        });

        user.setName(request.getFacultyName());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
        } else if (user.getPassword() == null) {
            user.setPassword("faculty123");
        }

        User savedUser = userRepository.save(user);

        Faculty faculty = facultyRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            Faculty newFaculty = new Faculty();
            newFaculty.setEmail(email);
            return newFaculty;
        });

        faculty.setFacultyName(request.getFacultyName());
        faculty.setDepartment(request.getDepartment());
        faculty.setUser(savedUser);

        facultyRepository.save(faculty);
        auditLogService.saveLog("Faculty Created/Updated", request.getFacultyName());

        return "Faculty Saved Successfully";
    }

    public String updateFaculty(Integer id, FacultyRequest request) {
        Faculty faculty = facultyRepository.findById(id).orElse(null);
        if (faculty == null) {
            return "Faculty Not Found";
        }

        String email = request.getEmail().trim();
        faculty.setFacultyName(request.getFacultyName());
        faculty.setDepartment(request.getDepartment());
        faculty.setEmail(email);

        User user = faculty.getUser();
        if (user != null) {
            user.setName(request.getFacultyName());
            user.setEmail(email);
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                user.setPassword(request.getPassword());
            }
            userRepository.save(user);
        }

        facultyRepository.save(faculty);
        auditLogService.saveLog("Faculty Updated", request.getFacultyName());
        return "Faculty Updated Successfully";
    }

    public String deleteFaculty(Integer id) {
        Faculty faculty = facultyRepository.findById(id).orElse(null);
        if (faculty != null) {
            String name = faculty.getFacultyName();
            User user = faculty.getUser();
            facultyRepository.delete(faculty);
            if (user != null) {
                userRepository.delete(user);
            }
            auditLogService.saveLog("Faculty Deleted", name);
            return "Faculty Deleted Successfully";
        }
        return "Faculty Not Found";
    }
}