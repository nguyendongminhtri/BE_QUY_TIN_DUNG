package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "song")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;
    @NotNull
    private String avatar;
    @NotNull
    @Lob
    private String lyrics;
    @NotNull
    private String URL;
    private int time;
    @ManyToOne
    Category category;
    @ManyToOne
    User user;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "song_singer", joinColumns = @JoinColumn(name = "songId"), inverseJoinColumns = @JoinColumn(name = "singerId"))
    private List<Singer> singerList = new ArrayList<>();
    public Song(String name,String avatar,String lyrics,String URL, Category category,List<Singer> singerList){
        this.name = name;
        this.avatar= avatar;
        this.lyrics = lyrics;
        this.URL = URL;
        this.category = category;
        this.singerList = singerList;
    }


}
