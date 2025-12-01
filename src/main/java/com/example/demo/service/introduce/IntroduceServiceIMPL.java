package com.example.demo.service.introduce;

import com.example.demo.model.CarouselEntity;
import com.example.demo.model.IntroduceEntity;
import com.example.demo.model.User;
import com.example.demo.repository.IntroduceRepository;
import com.example.demo.security.userprincal.UserDetailService;
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
public class IntroduceServiceIMPL implements IIntroduceService {
    @Autowired
    private IntroduceRepository introduceRepository;
    @Autowired
    private UserDetailService userDetailService;

    @Override
    public List<IntroduceEntity> findAll() {
        return introduceRepository.findAll();
    }

    @Override
    public void save(IntroduceEntity introduceEntity) {
        User user = userDetailService.getCurrentUser();
        introduceEntity.setUser(user);
        ObjectMapper mapper = new ObjectMapper();
        String jsonPaths = null;
        try {
            jsonPaths = mapper.writeValueAsString(introduceEntity.getContentStoragePathsJson());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        introduceEntity.setContentStoragePathsJson(jsonPaths);

        introduceRepository.save(introduceEntity);
    }

    @Override
    public Page<IntroduceEntity> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<IntroduceEntity> findById(Long id) {
        return introduceRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        introduceRepository.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Boolean isShow) {
        if (id == null || isShow == null) {
            throw new IllegalArgumentException("ID và trạng thái isShow không được null");
        }

        User user = userDetailService.getCurrentUser();
        Optional<IntroduceEntity> optionalCarousel = introduceRepository.findById(id);

        if (optionalCarousel.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy Introduce với ID: " + id);
        }

        IntroduceEntity introduceEntity = optionalCarousel.get();
        introduceEntity.setUser(user);
        introduceEntity.setIsShow(isShow);
        introduceRepository.save(introduceEntity);
    }
}
