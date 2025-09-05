package com.example.demo.config;

import com.example.demo.model.Song;

import java.util.Comparator;

public class ComparatorSong implements Comparator<Song>{
    @Override
    public int compare(Song o1, Song o2) {
        int temp = o1.getName().compareTo(o2.getName());
        return temp;
    }
}
