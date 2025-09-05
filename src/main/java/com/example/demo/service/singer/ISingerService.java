package com.example.demo.service.singer;

import com.example.demo.model.Singer;
import com.example.demo.service.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISingerService extends IGenericService<Singer> {
//    Boolean existsByName(String name);
Page<Singer> findAllByNameContaining(String name, Pageable pageable);
}
