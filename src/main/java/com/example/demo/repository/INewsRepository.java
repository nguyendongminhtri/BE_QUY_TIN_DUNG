package com.example.demo.repository;

import com.example.demo.model.NewsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface INewsRepository extends JpaRepository<NewsEntity, Long> {
    List<NewsEntity> findAllByCategoryId(Long categoryId);
    Page<NewsEntity> findAllByTitleContaining(String name, Pageable pageable);
}
