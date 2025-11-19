package com.example.demo.service.news;

import com.example.demo.model.NewsEntity;
import com.example.demo.service.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface INewsService extends IGenericService<NewsEntity> {
    List<NewsEntity> findAllByCategoryId(Long categoryId);

    Page<NewsEntity> findAllByTitleContaining(String title, Pageable pageable);
    void updateStatus(Long id, Boolean isShow);
    //    //    List<Song> getSongByLit3();
//    List<NewsEntity> getRanDomSong(int limit);
//
//    void saveView(NewsEntity song);
//
//    List<NewsEntity> findTop5ByOrderByTimeDesc();

}
