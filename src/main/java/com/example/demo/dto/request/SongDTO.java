package com.example.demo.dto.request;

import com.example.demo.model.Category;
import com.example.demo.model.Singer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongDTO {

    @NotBlank
    @NotNull
    private String name;
    @NotBlank
    @NotNull
    private String avatar;
    @NotBlank
    @NotNull
    private String lyrics;
    @NotBlank
    @NotNull
    private String URL;
    @NotNull
    private Category category;
    @NotNull
    private List<Singer> singerList = new ArrayList<>();
}
