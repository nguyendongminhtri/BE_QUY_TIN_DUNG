package com.example.demo.service.news;
import com.example.demo.model.CategoryEntity;
import com.example.demo.model.NewsEntity;
import com.example.demo.model.User;
import com.example.demo.repository.INewsRepository;
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
public class NewsServiceIMPL implements INewsService {
    @Autowired
    private INewsRepository newsRepository;

    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private ICategoryService categoryService;
    @Override
    public List<NewsEntity> findAll() {
        return newsRepository.findAll();
    }

    @Override
    public void save(NewsEntity news) {
        User user = userDetailService.getCurrentUser();
        news.setUser(user);

        if (news.getCategory() != null && news.getCategory().getId() != null) {
            CategoryEntity category = categoryService.findById(news.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("News not found"));
            news.setCategory(category);
        }

        // Serialize contentStoragePathsJson thành JSON string
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonPaths = mapper.writeValueAsString(news.getContentStoragePathsJson());
            news.setContentStoragePathsJson(jsonPaths);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        newsRepository.save(news);
    }


    @Override
    public Page<NewsEntity> findAll(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

    @Override
    public Optional<NewsEntity> findById(Long id) {
        return newsRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        newsRepository.deleteById(id);
    }

    @Override
    public Page<NewsEntity> findAllByCategoryId(Long categoryId, Pageable pageable) {
        return newsRepository.findAllByCategoryId(categoryId, pageable);
    }

    @Override
    public List<NewsEntity> findAllByCategoryId(Long categoryId) {
        return newsRepository.findAllByCategoryId(categoryId);
    }

    @Override
    public void updateStatus(Long id, Boolean isShow) {
        if (id == null || isShow == null) {
            throw new IllegalArgumentException("ID và trạng thái isShow không được null");
        }

        User user = userDetailService.getCurrentUser();
        Optional<NewsEntity> optionalNews = newsRepository.findById(id);

        if (optionalNews.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy News với ID: " + id);
        }

        NewsEntity newsEntity = optionalNews.get();
        newsEntity.setUser(user);
        newsEntity.setIsShow(isShow);
        newsRepository.save(newsEntity);
    }

    @Override
    public Page<NewsEntity> fullTextSearch(Long categoryId, String keyword, Pageable pageable) {
        return newsRepository.fullTextSearch(categoryId, keyword, pageable);
    }

}
