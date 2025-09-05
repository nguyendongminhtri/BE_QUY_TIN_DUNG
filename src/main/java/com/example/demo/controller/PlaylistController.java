package com.example.demo.controller;

import com.example.demo.config.ComparatorSong;
import com.example.demo.config.MessageConfig;
import com.example.demo.dto.request.PlaylistDTO;
import com.example.demo.dto.response.ResponMessage;

import com.example.demo.model.PlayList;
import com.example.demo.model.Song;
import com.example.demo.model.User;
import com.example.demo.security.userprincal.UserDetailService;
import com.example.demo.service.playlist.IPlaylistService;
import com.example.demo.service.song.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/playlist")
public class PlaylistController {
    @Autowired
    private IPlaylistService playlistService;
    @Autowired
    UserDetailService userDetailService;
    @Autowired
    private ISongService songService;

    @GetMapping
    public ResponseEntity<?> getListPlaylist() {
        return new ResponseEntity<>(playlistService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlaylistById(@PathVariable Long id) {
        Optional<PlayList> playList = playlistService.findById(id);
        if (!playList.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(playList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createPlaylist(@RequestBody PlayList playList) {
        playlistService.save(playList);
        return new ResponseEntity<>(new ResponMessage("create_success"), HttpStatus.OK);
    }

    @GetMapping("/playlist_user")
    public ResponseEntity<?> getPlaylistUser() {
        User user = userDetailService.getCurrentUser();
        return new ResponseEntity<>(playlistService.findAllByUserId(user.getId()), HttpStatus.OK);
    }
//ooo
    @PostMapping("/add-song")
    public ResponseEntity<?> addSongToPlaylist(@RequestBody PlaylistDTO playlistDTO) {
        Optional<Song> song = songService.findById(playlistDTO.getSong_id());

        Optional<PlayList> playList = playlistService.findById(playlistDTO.getPlaylist_id());
        List<Song> songList = new ArrayList<>();
        songList = playlistService.findByIdPlayList(playlistDTO.getPlaylist_id());  /// laay list cu
        songList.add(song.get());   // theem song mowis
        playList.get().setSongList(songList);
        ///
        playlistService.save(playList.get());
        return new ResponseEntity<>(new ResponMessage(MessageConfig.CREATE_SUCCESS), HttpStatus.OK);
    }

    @PutMapping("/delete-song")
    public ResponseEntity<?> deleteSongInPlaylist(@RequestBody PlaylistDTO playlistDTO) {
        Optional<Song> song = songService.findById(playlistDTO.getSong_id());
        Optional<PlayList> playList = playlistService.findById(playlistDTO.getPlaylist_id());
        List<Song> songList = new ArrayList<>();
        List<Song> songAfter = new ArrayList<>();
        songList = playlistService.findByIdPlayList(playlistDTO.getPlaylist_id());
        System.out.println("song lístr truoc khi xoa"+songList.size());
        for (int i = 0; i < songList.size(); i++) {
            if(songList.get(i).getId()==song.get().getId()){
               continue;
            }
                songAfter.add(songList.get(i));

        }
//        songList.remove(song.get());
        System.out.println("song lístr sau khi xoa"+songList.size());
        playList.get().setSongList(songAfter);
        playlistService.save(playList.get());
        return new ResponseEntity<>(new ResponMessage("delete_success"), HttpStatus.OK);

    }

    @GetMapping("/get-songList/{id}")
    public ResponseEntity<?> getListSong(@PathVariable Long id) {
        List<Song> songList = new ArrayList<>();
        Optional<PlayList> playList = playlistService.findById(id);
        songList = playlistService.findByIdPlayList(id);
        System.out.println(songList.size());
        ComparatorSong comparator = new ComparatorSong();
        Collections.sort(songList, comparator);


        return new ResponseEntity<>(songList, HttpStatus.OK);
    }


}
