package com.example.demo.controller;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.NewsEntity;
import com.example.demo.model.StorySuccessEntity;
import com.example.demo.service.storysuccess.IStorySuccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/story-success")
@CrossOrigin(origins = "*")
public class StorySuccessController {
    @Autowired
    private IStorySuccessService storySuccessService;

    @PostMapping
    public ResponseEntity<?> createStorySuccess(@Valid @RequestBody StorySuccessEntity storySuccessEntity) {
        storySuccessService.save(storySuccessEntity);
        return new ResponseEntity<>(new ResponMessage("create_success"), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> showListStorySuccess() {
        return new ResponseEntity<>(storySuccessService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStorySuccessById(@PathVariable Long id) {
        Optional<StorySuccessEntity> storySuccessEntity = storySuccessService.findById(id);
        if (!storySuccessEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(storySuccessEntity, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNews(@PathVariable Long id, @RequestBody StorySuccessEntity updatedData) {
        Optional<StorySuccessEntity> optionalNews = storySuccessService.findById(id);
        if (!optionalNews.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponMessage("news_not_found"));
        }
        StorySuccessEntity existing = optionalNews.get();
        existing.setTitle(updatedData.getTitle());
        existing.setDescription(updatedData.getDescription());
        existing.setContent(updatedData.getContent());
        existing.setImageUrl(updatedData.getImageUrl());
        existing.setImageStoragePath(updatedData.getImageStoragePath());
        existing.setContentStoragePathsJson(updatedData.getContentStoragePathsJson());
        existing.setIsShow(updatedData.getIsShow());
        existing.setCategory(updatedData.getCategory());
        storySuccessService.save(existing);
        return ResponseEntity.ok(new ResponMessage("update_success"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long id) {
        Optional<StorySuccessEntity> song = storySuccessService.findById(id);
        if (!song.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        storySuccessService.deleteById(id);
        return new ResponseEntity<>(new ResponMessage("delete_success"), HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateNewsStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> requestBody) {

        Boolean isShow = requestBody.get("isShow");
        if (isShow == null) {
            return ResponseEntity.badRequest().body("Trường 'isShow' không được để trống");
        }

        try {
            storySuccessService.updateStatus(id, isShow);
            return new ResponseEntity<>(new ResponMessage("update_success"), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy news với ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật trạng thái");
        }
    }
    @GetMapping("/by-category/{categoryId}")
    public Page<StorySuccessEntity> getNewsByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return storySuccessService.findAllByCategoryId(categoryId, pageable);
    }
    @GetMapping("/by-category/{categoryId}/search")
    public Page<StorySuccessEntity> searchByCategory(@PathVariable Long categoryId,
                                                     @RequestParam String keyword,
                                                     Pageable pageable) {
        return storySuccessService.fullTextSearch(categoryId, keyword, pageable);
    }
}
