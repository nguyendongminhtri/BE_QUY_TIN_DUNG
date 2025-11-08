package com.example.demo.controller;

import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.CarouselEntity;
import com.example.demo.service.carousel.ICarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Map;

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
}
