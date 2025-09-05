package com.example.demo.service.song;

import com.example.demo.dto.request.SongDTO;
import com.example.demo.model.Song;
import com.example.demo.model.User;
import com.example.demo.repository.ISongRepository;
import com.example.demo.security.userprincal.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service

public class SongServiceIMPL implements ISongService {
    @Autowired
    private ISongRepository songRepository;

    @Autowired
    private UserDetailService userDetailService;
    @PersistenceContext
    private EntityManager entityManager;

    public List<Song> getRanDomSong(int limit) {
        return entityManager.createQuery("SELECT p FROM Song p ORDER BY rand()",
                Song.class).setMaxResults(limit).getResultList();
    }
    @Override
    public List<Song> findAll() {
        return songRepository.findAll();
    }

    @Override
    public void save(Song song) {
        User user = userDetailService.getCurrentUser();
        song.setUser(user);
        songRepository.save(song);
    }

    @Override
    public Page<Song> findAll(Pageable pageable) {
        return songRepository.findAll(pageable);
    }

    @Override
    public Optional<Song> findById(Long id) {
        return songRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        songRepository.deleteById(id);
    }

    @Override
    public List<Song> findAllByCategoryId(Long categoryId) {
        return songRepository.findAllByCategoryId(categoryId);
    }

    @Override
    public Page<Song> findAllByNameContaining(String name, Pageable pageable) {
        return songRepository.findAllByNameContaining(name,pageable);
    }

//    @Override
//    public List<Song> getSongByLit3() {
//        return songRepository.getSongByLit3();
//    }

    @Override
    public void saveView(Song song) {
        songRepository.save(song);
    }

    @Override
    public List<Song> findTop5ByOrderByTimeDesc() {
        return songRepository.findTop5ByOrderByTimeDesc();
    }

}
