package com.example.demo.controller;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.CarouselEntity;
import com.example.demo.model.NewsEntity;
import com.example.demo.service.news.INewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private INewsService newsService;
    @GetMapping("/page")
    public ResponseEntity<?> pageSong(Pageable pageable) {
        return new ResponseEntity<>(newsService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> listNews() {
        return new ResponseEntity<>(newsService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createNews(@Valid @RequestBody NewsEntity newsEntity) {
        newsService.save(newsEntity);
        return new ResponseEntity<>(new ResponMessage("create_success"), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSongById(@PathVariable Long id) {
        Optional<NewsEntity> song = newsService.findById(id);
        if (!song.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(song, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCarousel(@PathVariable Long id, @RequestBody NewsEntity updatedData) {
        Optional<NewsEntity> optionalNews = newsService.findById(id);
        if (!optionalNews.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponMessage("news_not_found"));
        }
        NewsEntity existing = optionalNews.get();
        existing.setTitle(updatedData.getTitle());
        existing.setDescription(updatedData.getDescription());
        existing.setContent(updatedData.getContent());
        existing.setImageUrl(updatedData.getImageUrl());
        existing.setImageStoragePath(updatedData.getImageStoragePath());
        existing.setContentStoragePathsJson(updatedData.getContentStoragePathsJson());
        existing.setIsShow(updatedData.getIsShow());
        existing.setCategory(updatedData.getCategory());
        newsService.save(existing);
        return ResponseEntity.ok(new ResponMessage("update_success"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long id) {
        Optional<NewsEntity> song = newsService.findById(id);
        if (!song.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        newsService.deleteById(id);
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
            newsService.updateStatus(id, isShow);
            return new ResponseEntity<>(new ResponMessage("update_success"), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy news với ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật trạng thái");
        }
    }
}
