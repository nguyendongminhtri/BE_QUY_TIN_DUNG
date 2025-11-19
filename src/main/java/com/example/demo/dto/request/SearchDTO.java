package com.example.demo.dto.request;

import com.example.demo.model.Album;
import com.example.demo.model.CategoryEntity;
import com.example.demo.model.Singer;
import com.example.demo.model.NewsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchDTO {
    Page<NewsEntity> songPage;
    Page<CategoryEntity> categoryPage;
    Page<Album> albumPage;
    Page<Singer> singerPage;
}
