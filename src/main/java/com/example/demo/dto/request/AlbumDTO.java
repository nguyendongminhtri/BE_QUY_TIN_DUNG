package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  /// getter setter
@AllArgsConstructor  /// cont full
@NoArgsConstructor   ///// con ko t.so
public class AlbumDTO {
    private Long album_id;
    private Long song_id;
}
