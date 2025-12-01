package com.example.demo.controller;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.ProductEntity;
import com.example.demo.service.product.IProductService;
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
@RequestMapping("/product")
@CrossOrigin(origins = "*")
public class ProductController {
    @Autowired
    private IProductService productService;

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductEntity productEntity) {
        productService.save(productEntity);
        return new ResponseEntity<>(new ResponMessage("create_success"), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> showListProduct() {
        return new ResponseEntity<>(productService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<ProductEntity> productEntity = productService.findById(id);
        if (!productEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(productEntity, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductEntity updatedData) {
        Optional<ProductEntity> optionalNews = productService.findById(id);
        if (!optionalNews.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponMessage("product_not_found"));
        }
        ProductEntity existing = optionalNews.get();
        existing.setTitle(updatedData.getTitle());
        existing.setDescription(updatedData.getDescription());
        existing.setContent(updatedData.getContent());
        existing.setImageUrl(updatedData.getImageUrl());
        existing.setImageStoragePath(updatedData.getImageStoragePath());
        existing.setContentStoragePathsJson(updatedData.getContentStoragePathsJson());
        existing.setIsShow(updatedData.getIsShow());
        existing.setCategory(updatedData.getCategory());
        productService.save(existing);
        return ResponseEntity.ok(new ResponMessage("update_success"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Optional<ProductEntity> song = productService.findById(id);
        if (!song.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        productService.deleteById(id);
        return new ResponseEntity<>(new ResponMessage("delete_success"), HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateProductStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> requestBody) {

        Boolean isShow = requestBody.get("isShow");
        if (isShow == null) {
            return ResponseEntity.badRequest().body("Trường 'isShow' không được để trống");
        }

        try {
            productService.updateStatus(id, isShow);
            return new ResponseEntity<>(new ResponMessage("update_success"), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy product với ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật trạng thái");
        }
    }

    @GetMapping("/by-category/{categoryId}")
    public Page<ProductEntity> getProductByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return productService.findAllByCategoryId(categoryId, pageable);
    }

    @GetMapping("/by-category/{categoryId}/search")
    public Page<ProductEntity> searchByCategory(@PathVariable Long categoryId,
                                                @RequestParam String keyword,
                                                Pageable pageable) {
        return productService.fullTextSearch(categoryId, keyword, pageable);
    }
}
