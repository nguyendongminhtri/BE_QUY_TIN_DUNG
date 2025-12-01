package com.example.demo.service.storysuccess;

import com.example.demo.model.CategoryEntity;
import com.example.demo.model.NewsEntity;
import com.example.demo.model.StorySuccessEntity;
import com.example.demo.model.User;
import com.example.demo.repository.IStorySuccessRepository;
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
public class StorySuccessServiceIMPL implements IStorySuccessService {
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IStorySuccessRepository storySuccessRepository;

    @Override
    public List<StorySuccessEntity> findAll() {
        return storySuccessRepository.findAll();
    }

    @Override
    public void save(StorySuccessEntity storySuccessEntity) {
        User user = userDetailService.getCurrentUser();
        storySuccessEntity.setUser(user);

        if (storySuccessEntity.getCategory() != null && storySuccessEntity.getCategory().getId() != null) {
            CategoryEntity category = categoryService.findById(storySuccessEntity.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("News not found"));
            storySuccessEntity.setCategory(category);
        }
        // Serialize contentStoragePathsJson thành JSON string
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonPaths = mapper.writeValueAsString(storySuccessEntity.getContentStoragePathsJson());
            storySuccessEntity.setContentStoragePathsJson(jsonPaths);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        storySuccessRepository.save(storySuccessEntity);
    }

    @Override
    public Page<StorySuccessEntity> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<StorySuccessEntity> findById(Long id) {
        return storySuccessRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        storySuccessRepository.deleteById(id);
    }

    @Override
    public Page<StorySuccessEntity> findAllByCategoryId(Long categoryId, Pageable pageable) {
        return storySuccessRepository.findAllByCategoryId(categoryId, pageable);
    }

    @Override
    public void updateStatus(Long id, Boolean isShow) {
        if (id == null || isShow == null) {
            throw new IllegalArgumentException("ID và trạng thái isShow không được null");
        }

        User user = userDetailService.getCurrentUser();
        Optional<StorySuccessEntity> optionalNews = storySuccessRepository.findById(id);

        if (optionalNews.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy Story Success với ID: " + id);
        }

        StorySuccessEntity newsEntity = optionalNews.get();
        newsEntity.setUser(user);
        newsEntity.setIsShow(isShow);
        storySuccessRepository.save(newsEntity);
    }

    @Override
    public Page<StorySuccessEntity> fullTextSearch(Long categoryId, String keyword, Pageable pageable) {
        return storySuccessRepository.fullTextSearch(categoryId, keyword,pageable);
    }
}
