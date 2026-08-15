package com.smartclassroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartclassroom.entity.Subject;
import com.smartclassroom.repository.SubjectRepository;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    @PostMapping
    public Subject createSubject(
            @RequestBody Subject subject) {

        return subjectRepository.save(subject);
    }

    @GetMapping
    public List<Subject> getAllSubjects() {

        return subjectRepository.findAll();
    }
}