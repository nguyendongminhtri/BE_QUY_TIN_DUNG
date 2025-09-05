package com.example.demo.service.song;

import com.example.demo.dto.request.SongDTO;
import com.example.demo.model.Song;
import com.example.demo.service.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ISongService extends IGenericService<Song> {
    List<Song> findAllByCategoryId(Long categoryId);

    Page<Song> findAllByNameContaining(String name, Pageable pageable);

    //    List<Song> getSongByLit3();
    List<Song> getRanDomSong(int limit);

    void saveView(Song song);

    List<Song> findTop5ByOrderByTimeDesc();

}
