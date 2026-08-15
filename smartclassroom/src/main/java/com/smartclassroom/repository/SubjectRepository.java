package com.smartclassroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartclassroom.entity.Subject;

public interface SubjectRepository
        extends JpaRepository<Subject, Integer> {

}