package com.example.demo.service.playlist;

import com.example.demo.model.PlayList;
import com.example.demo.model.NewsEntity;
import com.example.demo.service.IGenericService;

import java.util.List;

public interface IPlaylistService extends IGenericService<PlayList> {
    List<PlayList> findAllByUserId(Long userId);
    List<NewsEntity> findByIdPlayList(Long id);
}
