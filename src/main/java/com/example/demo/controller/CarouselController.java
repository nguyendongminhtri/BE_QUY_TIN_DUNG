package com.example.demo.controller;

import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.CarouselEntity;
import com.example.demo.service.carousel.ICarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/carousel")
public class CarouselController {
    @Autowired
    private ICarouselService carouselService;
    @PostMapping("/create")
    public ResponseEntity<?> createCarousel(@RequestBody CarouselEntity carouselEntity){
        carouselService.save(carouselEntity);
        return new ResponseEntity<>(new ResponMessage("success"), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<?> getAllCarousel(){
        return new ResponseEntity<>(carouselService.findAll(), HttpStatus.OK);
    }
}
