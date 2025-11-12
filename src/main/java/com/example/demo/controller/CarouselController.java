package com.example.demo.controller;

import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.CarouselEntity;
import com.example.demo.model.Category;
import com.example.demo.service.carousel.ICarouselService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.Optional;

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
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateCarouselStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> requestBody) {

        Boolean isShow = requestBody.get("isShow");
        if (isShow == null) {
            return ResponseEntity.badRequest().body("Trường 'isShow' không được để trống");
        }

        try {
            carouselService.updateStatus(id, isShow);
            return new ResponseEntity<>(new ResponMessage("update_success"), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy carousel với ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật trạng thái");
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCarousel(@PathVariable Long id) {
        Optional<CarouselEntity> carouselEntity = carouselService.findById(id);
        if(!carouselEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        carouselService.deleteById(id);
        return new ResponseEntity<>(new ResponMessage("delete_success"), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getCarouselById(@PathVariable Long id) {
        Optional<CarouselEntity> carouselEntity = carouselService.findById(id);
        if(!carouselEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(carouselEntity.get(), HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCarousel(@PathVariable Long id, @RequestBody CarouselEntity updatedData) {
        Optional<CarouselEntity> optionalCarousel = carouselService.findById(id);
        if (!optionalCarousel.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponMessage("carousel_not_found"));
        }
        CarouselEntity existing = optionalCarousel.get();
        existing.setTitle(updatedData.getTitle());
        existing.setDescription(updatedData.getDescription());
        existing.setContent(updatedData.getContent());
        existing.setImageUrl(updatedData.getImageUrl());
        existing.setImageStoragePath(updatedData.getImageStoragePath());
        existing.setContentStoragePathsJson(updatedData.getContentStoragePathsJson());
        existing.setIsShow(updatedData.getIsShow());
        carouselService.save(existing);
        return ResponseEntity.ok(new ResponMessage("update_success"));
    }

}
