package com.example.demo.repository;

import com.example.demo.model.NewsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface INewsRepository extends JpaRepository<NewsEntity, Long> {
    Page<NewsEntity> findAllByCategoryId(Long categoryId, Pageable pageable);
    List<NewsEntity> findAllByCategoryId(Long categoryId);
    @Query(value = "SELECT * FROM news " +
            "WHERE category_id = :categoryId " +
            "AND (MATCH(title, description, content) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
            "OR title LIKE CONCAT('%', :keyword, '%'))",
            countQuery = "SELECT COUNT(*) FROM news " +
                    "WHERE category_id = :categoryId " +
                    "AND (MATCH(title, description, content) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
                    "OR title LIKE CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    Page<NewsEntity> fullTextSearch(@Param("categoryId") Long categoryId,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);

}
