package com.example.demo.repository;

import com.example.demo.model.GiaoDichEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGiaoDichRepository extends JpaRepository<GiaoDichEntity, String> {
}
