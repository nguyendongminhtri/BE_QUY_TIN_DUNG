package com.example.demo.repository;

import com.example.demo.model.CarouselEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICarouselRepository extends JpaRepository<CarouselEntity, Long> {
}
