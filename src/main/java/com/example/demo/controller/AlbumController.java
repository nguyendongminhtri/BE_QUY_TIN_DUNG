package com.example.demo.controller;

import com.example.demo.config.ComparatorSong;
import com.example.demo.config.MessageConfig;
import com.example.demo.dto.request.AlbumDTO;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.Album;
import com.example.demo.model.Category;
import com.example.demo.model.Song;
import com.example.demo.model.User;
import com.example.demo.security.userprincal.UserDetailService;
import com.example.demo.service.album.IAlbumService;
import com.example.demo.service.song.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/album")
public class AlbumController {
    @Autowired
    private IAlbumService albumService;
    @Autowired
    UserDetailService userDetailService;
    @Autowired
    private ISongService songService;

    @GetMapping
    public ResponseEntity<?> getListAlbum() {
        return new ResponseEntity<>(albumService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlbumById(@PathVariable Long id) {
        Optional<Album> album = albumService.findById(id);
        if (!album.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(album, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createAlbum(@RequestBody Album album) {
        if (albumService.existsByName(album.getName())){
            return  new ResponseEntity<>(new ResponMessage("name_exist"), HttpStatus.OK);
        }
        albumService.save(album);
        return new ResponseEntity<>(new ResponMessage("create_success"), HttpStatus.OK);
    }

    @GetMapping("/album_user")
    public ResponseEntity<?> getPlaylistUser() {
        User user = userDetailService.getCurrentUser();
        return new ResponseEntity<>(albumService.findAllByUserId(user.getId()), HttpStatus.OK);
    }
    //ooo
    @PostMapping("/add-song")
    public ResponseEntity<?> addSongToAlbum(@RequestBody AlbumDTO albumDTO) {
        Optional<Song> song = songService.findById(albumDTO.getSong_id());

        Optional<Album> album = albumService.findById(albumDTO.getAlbum_id());
        List<Song> songList = new ArrayList<>();
        songList = albumService.findByIdAlbum(albumDTO.getAlbum_id());  /// laay list cu
        songList.add(song.get());   // theem song mowis
        album.get().setSongList(songList);
        ///
        albumService.save(album.get());
        return new ResponseEntity<>(new ResponMessage(MessageConfig.CREATE_SUCCESS), HttpStatus.OK);
    }

    @PutMapping("/delete-song")
    public ResponseEntity<?> deleteSongInPlaylist(@RequestBody AlbumDTO albumDTO) {
        Optional<Song> song = songService.findById(albumDTO.getSong_id());
        Optional<Album> album = albumService.findById(albumDTO.getAlbum_id());
        List<Song> songList = new ArrayList<>();
        List<Song> songAfter = new ArrayList<>();
        songList = albumService.findByIdAlbum(albumDTO.getAlbum_id());
        for (int i = 0; i < songList.size(); i++) {
            if(songList.get(i).getId()==song.get().getId()){
                continue;
            }
            songAfter.add(songList.get(i));

        }
//        songList.remove(song.get());
        System.out.println("song lístr sau khi xoa"+songList.size());
        album.get().setSongList(songAfter);
        albumService.save(album.get());
        return new ResponseEntity<>(new ResponMessage("delete_success"), HttpStatus.OK);

    }

    @GetMapping("/get-songList/{id}")
    public ResponseEntity<?> getListSong(@PathVariable Long id) {
        List<Song> songList = new ArrayList<>();
        Optional<Album> album = albumService.findById(id);
        songList = albumService.findByIdAlbum(id);
        System.out.println(songList.size());
        ComparatorSong comparator = new ComparatorSong();
        Collections.sort(songList, comparator);


        return new ResponseEntity<>(songList, HttpStatus.OK);
    }
    @GetMapping("/page")
    public ResponseEntity<?> pageAlbum(@PageableDefault(size = 3) Pageable pageable){
        return new ResponseEntity<>(albumService.findAll(pageable), HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAlbum(@PathVariable Long id, @RequestBody Album album){
        Optional<Album> album1 = albumService.findById(id);
        if(!album1.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(!album.getAvatar().equals(album1.get().getAvatar())){
            album.setId(album1.get().getId());
        }
        if(!album.getName().equals(album1.get().getName())){
            if(albumService.existsByName(album.getName())) {
                return new ResponseEntity<>(new ResponMessage("name_existed"), HttpStatus.OK);
            }
        }
        if(album.getName().equals(album1.get().getName())&& album.getAvatar().equals(album1.get().getAvatar())){
            return new ResponseEntity<>(new ResponMessage("no_change"), HttpStatus.OK);
        }
        album.setId(album1.get().getId());
        albumService.save(album);
        return new ResponseEntity<>(new ResponMessage("update_success"), HttpStatus.OK);
    }

}
