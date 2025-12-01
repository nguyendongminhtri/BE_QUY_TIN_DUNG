package com.example.demo.service.storysuccess;

import com.example.demo.model.NewsEntity;
import com.example.demo.model.StorySuccessEntity;
import com.example.demo.service.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface IStorySuccessService extends IGenericService<StorySuccessEntity> {
    Page<StorySuccessEntity> findAllByCategoryId(Long categoryId, Pageable pageable);
    void updateStatus(Long id, Boolean isShow);
    Page<StorySuccessEntity> fullTextSearch(@Param("categoryId") Long categoryId,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);
}
