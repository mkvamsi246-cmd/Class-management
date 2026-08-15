package com.smartclassroom.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartclassroom.dto.ChangePasswordRequest;
import com.smartclassroom.dto.LoginRequest;
import com.smartclassroom.dto.LoginResponse;
import com.smartclassroom.entity.Faculty;
import com.smartclassroom.entity.User;
import com.smartclassroom.repository.FacultyRepository;
import com.smartclassroom.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    public LoginResponse login(LoginRequest request) {

        Optional<User> userOptional =
                userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User Not Found");
        }

        User user = userOptional.get();

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }

        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setMessage("Login Successful");

        // If the user is a faculty member, also return the Faculty entity id
        // so the frontend can use it for timetable and request lookups
        Optional<Faculty> facultyOpt =
                facultyRepository.findByUserId(user.getId());

        facultyOpt.ifPresent(f -> response.setFacultyId(f.getId()));

        return response;
    }

    public String changePassword(ChangePasswordRequest request) {

        User user = userRepository
                .findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        if (!user.getPassword().equals(request.getOldPassword())) {
            throw new RuntimeException("Old Password Incorrect");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        return "Password Changed Successfully";
    }
}