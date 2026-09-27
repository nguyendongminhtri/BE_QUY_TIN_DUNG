package com.example.demo.service.category;

import com.example.demo.model.CategoryEntity;
import com.example.demo.service.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryService extends IGenericService<CategoryEntity> {
    Boolean existsByName(String name);
    Page<CategoryEntity> findAllByNameContaining(String name, Pageable pageable);
}
