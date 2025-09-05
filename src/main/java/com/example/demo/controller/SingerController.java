package com.example.demo.controller;

import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.Singer;
import com.example.demo.model.Song;
import com.example.demo.service.singer.ISingerService;
import com.example.demo.service.song.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/singer")


public class SingerController {
    @Autowired
    private ISingerService singerService;
    @Autowired
    private ISongService songService;

    @GetMapping("/page")
    public ResponseEntity<?> pageSinger(Pageable pageable) {
//        fix
        return new ResponseEntity<>(singerService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> listSinger() {
        return new ResponseEntity<>(singerService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detailSinger(@PathVariable Long id){
        Optional<Singer> singer = singerService.findById(id);
        if(!singer.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(singer, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createSinger(@Valid @RequestBody Singer singer) {
        singerService.save(singer);
        return new ResponseEntity<>(new ResponMessage("create_success"), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSinger(@PathVariable Long id, @RequestBody Singer singer) {
        Optional<Singer> singer1 = singerService.findById(id);
        if (!singer1.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (singer.getName().equals(singer1.get().getName()) &&
                singer.getAvatar().equals(singer1.get().getAvatar()) &&
                singer.getDescription().equals(singer1.get().getDescription()) &&
                singer.getSex().equals(singer1.get().getSex()) &&
                singer.getNation().equals(singer1.get().getNation()) &&
                singer.getBirthday().equals(singer1.get().getBirthday())) {
            return new ResponseEntity<>(new ResponMessage("no_change"), HttpStatus.OK);
        }
        if (!singer.getAvatar().equals(singer1.get().getAvatar())) {
            singer1.get().setAvatar(singer.getAvatar());
        }
        if (!singer.getDescription().equals(singer1.get().getDescription())) {
            singer1.get().setDescription(singer.getDescription());
        }
        if (!singer.getSex().equals(singer1.get().getSex())) {
            singer1.get().setSex(singer.getSex());
        }
        if (!singer.getNation().equals(singer1.get().getNation())) {
            singer1.get().setNation(singer.getNation());
        }
        if (!singer.getBirthday().equals(singer1.get().getBirthday())) {
            singer1.get().setBirthday(singer.getBirthday());
        }
        if (!singer.getName().equals(singer1.get().getName())) {
           singer1.get().setName(singer.getName());
        }

//        singer.setId(singer1.get().getId());
        singerService.save(singer1.get());
        return new ResponseEntity<>(new ResponMessage("update_success"), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSinger(@PathVariable Long id) {
        Optional<Singer> singer = singerService.findById(id);
        if (!singer.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Song> songList = songService.findAll();
        for (int i = 0; i < songList.size(); i++) {
            if (!songList.get(i).getSingerList().isEmpty()) {
                List<Singer> singerList = songList.get(i).getSingerList(); /// tao mang trung gian
                for (int j = 0; j < songList.get(i).getSingerList().size(); j++) {
                    if (songList.get(i).getSingerList().get(j).getId() == id) {
                        singerList.remove(singerList.get(j));
                    }
                }
                songList.get(i).setSingerList(singerList);
                songService.save(songList.get(i));

            }
        }
        singerService.deleteById(id);
        return new ResponseEntity<>(new ResponMessage("delete_success"), HttpStatus.OK);
    }
}
