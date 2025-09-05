package com.example.demo.dto.request;

import com.example.demo.model.Album;
import com.example.demo.model.Category;
import com.example.demo.model.Singer;
import com.example.demo.model.Song;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchDTO {
    Page<Song> songPage;
    Page<Category> categoryPage;
    Page<Album> albumPage;
    Page<Singer> singerPage;
}
