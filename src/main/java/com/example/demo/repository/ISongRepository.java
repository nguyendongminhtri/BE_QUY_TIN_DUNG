package com.example.demo.repository;

import com.example.demo.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Repository
public interface ISongRepository extends JpaRepository<Song, Long> {
    List<Song> findAllByCategoryId(Long categoryId);


    ///findAllByNameContaining  : tìm kiem gan dung : sql like %
    Page<Song> findAllByNameContaining(String name, Pageable pageable);

    List<Song> findTop5ByOrderByTimeDesc();
}
