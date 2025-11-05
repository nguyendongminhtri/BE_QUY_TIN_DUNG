package com.example.demo.service.carousel;

import com.example.demo.model.CarouselEntity;
import com.example.demo.model.User;
import com.example.demo.repository.ICarouselRepository;
import com.example.demo.security.userprincal.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {

    }
}
