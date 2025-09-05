package com.example.demo.service.playlist;

import com.example.demo.model.PlayList;
import com.example.demo.model.Song;
import com.example.demo.service.IGenericService;

import java.util.List;

public interface IPlaylistService extends IGenericService<PlayList> {
    List<PlayList> findAllByUserId(Long userId);
    List<Song> findByIdPlayList(Long id);
}
