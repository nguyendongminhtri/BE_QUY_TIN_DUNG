package com.example.demo.service.playlist;

import com.example.demo.model.PlayList;
import com.example.demo.model.Song;
import com.example.demo.model.User;
import com.example.demo.repository.IPlaylistRepository;
import com.example.demo.security.userprincal.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistServiceIMPL implements IPlaylistService {
    @Autowired
    private IPlaylistRepository playlistRepository;
    @Autowired
    private UserDetailService userDetailService;

    @Override
    public List<PlayList> findAll() {
        return playlistRepository.findAll();
    }

    @Override
    public void save(PlayList playList) {
        User user = userDetailService.getCurrentUser();
        playList.setUser(user);
        playlistRepository.save(playList);
    }

    @Override
    public Page<PlayList> findAll(Pageable pageable) {
        return playlistRepository.findAll(pageable);
    }

    @Override
    public Optional<PlayList> findById(Long id) {
        return playlistRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        playlistRepository.deleteById(id);
    }


    @Override
    public List<PlayList> findAllByUserId(Long userId) {
        return playlistRepository.findAllByUserId(userId);
    }

    @Override
    public List<Song> findByIdPlayList(Long id) {
        return playlistRepository.findByIdPlayList(id);
    }
}
