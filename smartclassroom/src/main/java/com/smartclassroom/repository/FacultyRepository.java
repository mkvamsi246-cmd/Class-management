package com.smartclassroom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartclassroom.entity.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {

    Optional<Faculty> findByUserId(Integer userId);

    Optional<Faculty> findByFacultyNameIgnoreCase(String facultyName);

    Optional<Faculty> findByEmailIgnoreCase(String email);
}