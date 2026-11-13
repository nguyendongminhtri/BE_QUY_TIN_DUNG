package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SyncHuyDongRequest(
        String ky,
        LocalDate tu,
        LocalDate den,
        List<Row> rows) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Row(
            String maChiNhanh,
            String maTk,
            String tenTk,
            long duDauKy,
            long psNo,
            long psCo,
            long duCuoiKy,
            long tangRong) {}
}
