package com.example.demo.repository;

import com.example.demo.model.ProductEntity;
import com.example.demo.model.StorySuccessEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository extends JpaRepository<ProductEntity, Long> {
    Page<ProductEntity> findAllByCategoryId(Long categoryId, Pageable pageable);
    @Query(value = "SELECT * FROM product " +
            "WHERE category_id = :categoryId " +
            "AND (MATCH(title, description, content) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
            "OR title LIKE CONCAT('%', :keyword, '%'))",
            countQuery = "SELECT COUNT(*) FROM product " +
                    "WHERE category_id = :categoryId " +
                    "AND (MATCH(title, description, content) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
                    "OR title LIKE CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    Page<ProductEntity> fullTextSearch(@Param("categoryId") Long categoryId,
                                            @Param("keyword") String keyword,
                                            Pageable pageable);
}
