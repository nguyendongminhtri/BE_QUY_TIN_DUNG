package com.example.demo.controller;

import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.CarouselEntity;
import com.example.demo.model.IntroduceEntity;
import com.example.demo.service.introduce.IIntroduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/introduce")
public class IntroduceController {
    @Autowired
    private IIntroduceService introduceService;
    @PostMapping
    public ResponseEntity<?> createIntroduce(@RequestBody IntroduceEntity introduceEntity){
        introduceService.save(introduceEntity);
        return new ResponseEntity<>(new ResponMessage("success"), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<?> getAllIntroduce(){
        return new ResponseEntity<>(introduceService.findAll(), HttpStatus.OK);
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateIntroduceStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> requestBody) {

        Boolean isShow = requestBody.get("isShow");
        if (isShow == null) {
            return ResponseEntity.badRequest().body("Trường 'isShow' không được để trống");
        }

        try {
            introduceService.updateStatus(id, isShow);
            return new ResponseEntity<>(new ResponMessage("update_success"), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy introduce với ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật trạng thái");
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIntroduce(@PathVariable Long id) {
        Optional<IntroduceEntity> introduceEntity = introduceService.findById(id);
        if(!introduceEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        introduceService.deleteById(id);
        return new ResponseEntity<>(new ResponMessage("delete_success"), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getIntroduceById(@PathVariable Long id) {
        Optional<IntroduceEntity> introduceEntity = introduceService.findById(id);
        if(!introduceEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(introduceEntity.get(), HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateIntroduce(@PathVariable Long id, @RequestBody IntroduceEntity introduceUpdate) {
        Optional<IntroduceEntity> optionalCarousel = introduceService.findById(id);
        if (!optionalCarousel.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponMessage("introduce_not_found"));
        }
        IntroduceEntity existing = optionalCarousel.get();
        existing.setTitle(introduceUpdate.getTitle());
        existing.setContent(introduceUpdate.getContent());
        existing.setContentStoragePathsJson(introduceUpdate.getContentStoragePathsJson());
        existing.setIsShow(introduceUpdate.getIsShow());
        introduceService.save(existing);
        return ResponseEntity.ok(new ResponMessage("update_success"));
    }
}
