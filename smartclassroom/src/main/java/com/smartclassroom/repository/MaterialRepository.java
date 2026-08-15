package com.smartclassroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartclassroom.entity.Material;

public interface MaterialRepository
        extends JpaRepository<Material, Integer> {

}