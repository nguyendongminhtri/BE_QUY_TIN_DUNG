package com.example.demo.service.album;

import com.example.demo.model.Album;
import com.example.demo.model.Song;
import com.example.demo.service.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IAlbumService extends IGenericService<Album> {
    Boolean existsByName(String name);
    List<Album> findAllByUserId(Long userId);
    Page<Album> findAllByNameContaining(String name, Pageable pageable);
    List<Song> findByIdAlbum(Long id);
}
