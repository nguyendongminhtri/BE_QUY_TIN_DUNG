package com.example.demo.service.carousel;

import com.example.demo.model.CarouselEntity;
import com.example.demo.service.IGenericService;

public interface ICarouselService extends IGenericService<CarouselEntity> {
   void updateStatus(Long id, Boolean isShow);
}
