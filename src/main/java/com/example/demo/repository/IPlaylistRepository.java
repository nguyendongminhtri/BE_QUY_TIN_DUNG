package com.example.demo.repository;

import com.example.demo.model.PlayList;
import com.example.demo.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPlaylistRepository extends JpaRepository<PlayList, Long> {
    List<PlayList> findAllByUserId(Long userId);
    @Query("select pll.songList from PlayList pll WHERE pll.id = :id")
    List<Song> findByIdPlayList(Long id);

}
