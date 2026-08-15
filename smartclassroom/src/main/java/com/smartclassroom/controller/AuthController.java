package com.smartclassroom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartclassroom.dto.ChangePasswordRequest;
import com.smartclassroom.dto.LoginRequest;
import com.smartclassroom.dto.LoginResponse;
import com.smartclassroom.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        return authService.login(request);
    }
    @PostMapping("/change-password")
public String changePassword(
        @RequestBody ChangePasswordRequest request) {

    return authService.changePassword(request);
}
}