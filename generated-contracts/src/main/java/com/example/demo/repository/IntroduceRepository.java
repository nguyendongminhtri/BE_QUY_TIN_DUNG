package com.example.demo.repository;

import com.example.demo.model.IntroduceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntroduceRepository extends JpaRepository<IntroduceEntity, Long> {
}
