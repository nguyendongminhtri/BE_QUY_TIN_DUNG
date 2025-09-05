package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "album")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String avatar;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "album_song",joinColumns = @JoinColumn(name = "albumId"), inverseJoinColumns = @JoinColumn(name = "songId"))
    private List<Song> songList =  new ArrayList<>();
    @ManyToOne
    private User user;
}
