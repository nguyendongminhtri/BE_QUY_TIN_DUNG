package com.example.demo.service.introduce;

import com.example.demo.model.IntroduceEntity;
import com.example.demo.service.IGenericService;

public interface IIntroduceService extends IGenericService<IntroduceEntity> {
    void updateStatus(Long id, Boolean isShow);
}
