package com.example.demo.service.product;

import com.example.demo.model.CategoryEntity;
import com.example.demo.model.ProductEntity;
import com.example.demo.model.User;
import com.example.demo.repository.IProductRepository;
import com.example.demo.security.userprincal.UserDetailService;
import com.example.demo.service.category.ICategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceIMPL implements IProductService {
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IProductRepository productRepository;

    @Override
    public Page<ProductEntity> findAllByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findAllByCategoryId(categoryId,pageable);
    }

    @Override
    public void updateStatus(Long id, Boolean isShow) {
        if (id == null || isShow == null) {
            throw new IllegalArgumentException("ID và trạng thái isShow không được null");
        }

        User user = userDetailService.getCurrentUser();
        Optional<ProductEntity> optionalNews = productRepository.findById(id);

        if (optionalNews.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy Story Success với ID: " + id);
        }

        ProductEntity newsEntity = optionalNews.get();
        newsEntity.setUser(user);
        newsEntity.setIsShow(isShow);
        productRepository.save(newsEntity);
    }

    @Override
    public Page<ProductEntity> fullTextSearch(Long categoryId, String keyword, Pageable pageable) {
        return productRepository.fullTextSearch(categoryId, keyword, pageable);
    }

    @Override
    public List<ProductEntity> findAll() {
        return productRepository.findAll();
    }

    @Override
    public void save(ProductEntity productEntity) {
        User user = userDetailService.getCurrentUser();
        productEntity.setUser(user);

        if (productEntity.getCategory() != null && productEntity.getCategory().getId() != null) {
            CategoryEntity category = categoryService.findById(productEntity.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("News not found"));
            productEntity.setCategory(category);
        }
        // Serialize contentStoragePathsJson thành JSON string
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonPaths = mapper.writeValueAsString(productEntity.getContentStoragePathsJson());
            productEntity.setContentStoragePathsJson(jsonPaths);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        productRepository.save(productEntity);
    }

    @Override
    public Page<ProductEntity> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Optional<ProductEntity> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
