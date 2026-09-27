package com.example.demo.repository;

import com.example.demo.model.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IQuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q WHERE q.level = :level AND q.isActive = true AND q.id NOT IN :exclude ORDER BY RANDOM()")
    List<Question> findRandom(@Param("level") int level, @Param("exclude")
    List<Long> exclude, Pageable pageable);
}
