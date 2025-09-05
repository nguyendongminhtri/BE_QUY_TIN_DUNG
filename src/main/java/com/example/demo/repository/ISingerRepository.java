package com.example.demo.repository;

import com.example.demo.model.Singer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISingerRepository extends JpaRepository<Singer,Long> {
    Boolean existsByName(String name);
    Page<Singer> findAllByNameContaining(String name, Pageable pageable);
}
