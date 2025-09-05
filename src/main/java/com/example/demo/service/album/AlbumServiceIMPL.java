package com.example.demo.service.album;

import com.example.demo.model.Album;
import com.example.demo.model.Song;
import com.example.demo.model.User;
import com.example.demo.repository.IAlbumRepository;
import com.example.demo.security.userprincal.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumServiceIMPL implements IAlbumService {
    @Autowired
    IAlbumRepository albumRepository;
    @Autowired
    private UserDetailService userDetailService;

    @Override
    public List<Album> findAll() {
        return albumRepository.findAll();
    }

    @Override
    public void save(Album album) {
        User user = userDetailService.getCurrentUser();
        album.setUser(user);
        albumRepository.save(album);
    }

    @Override
    public Page<Album> findAll(Pageable pageable) {
        return albumRepository.findAll(pageable);
    }

    @Override
    public Optional<Album> findById(Long id) {
        return albumRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        albumRepository.deleteById(id);
    }

    @Override
    public Boolean existsByName(String name) {
        return albumRepository.existsByName(name);
    }

    @Override
    public List<Album> findAllByUserId(Long userId) {
        return albumRepository.findAllByUserId(userId);
    }

    @Override
    public Page<Album> findAllByNameContaining(String name, Pageable pageable) {
        return albumRepository.findAllByNameContaining(name, pageable);
    }

    @Override
    public List<Song> findByIdAlbum(Long id) {
        return albumRepository.findByIdAlbum(id);
    }
}
