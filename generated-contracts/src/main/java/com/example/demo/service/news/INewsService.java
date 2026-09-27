package com.example.demo.service.news;

import com.example.demo.model.NewsEntity;
import com.example.demo.service.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface INewsService extends IGenericService<NewsEntity> {
    Page<NewsEntity> findAllByCategoryId(Long categoryId, Pageable pageable);
    List<NewsEntity> findAllByCategoryId(Long categoryId);
    void updateStatus(Long id, Boolean isShow);
    Page<NewsEntity> fullTextSearch(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, Pageable pageable);
}
