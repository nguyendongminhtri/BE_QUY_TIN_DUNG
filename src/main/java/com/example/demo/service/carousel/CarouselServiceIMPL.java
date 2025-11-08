package com.example.demo.service.carousel;

import com.example.demo.model.CarouselEntity;
import com.example.demo.model.User;
import com.example.demo.repository.ICarouselRepository;
import com.example.demo.security.userprincal.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class CarouselServiceIMPL implements ICarouselService{
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private ICarouselRepository carouselRepository;
    @Override
    public List<CarouselEntity> findAll() {
        return carouselRepository.findAll();
    }

    @Override
    public void save(CarouselEntity carouselEntity) {
        User user = userDetailService.getCurrentUser();
        carouselEntity.setUser(user);
        carouselRepository.save(carouselEntity);
    }

    @Override
    public Page<CarouselEntity> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<CarouselEntity> findById(Long id) {
        return carouselRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void updateStatus(Long id, Boolean isShow) {
        if (id == null || isShow == null) {
            throw new IllegalArgumentException("ID và trạng thái isShow không được null");
        }

        User user = userDetailService.getCurrentUser();
        Optional<CarouselEntity> optionalCarousel = carouselRepository.findById(id);

        if (optionalCarousel.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy Carousel với ID: " + id);
        }

        CarouselEntity carouselEntity = optionalCarousel.get();
        carouselEntity.setUser(user);
        carouselEntity.setIsShow(isShow);
        carouselRepository.save(carouselEntity);
    }

}
