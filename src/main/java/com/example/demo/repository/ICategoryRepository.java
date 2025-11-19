package com.example.demo.repository;

import com.example.demo.model.CategoryEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Boolean existsByName(String name);
    Page<CategoryEntity> findAllByNameContaining(String name, Pageable pageable);
}