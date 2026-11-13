package com.example.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "huy_dong_ky",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ky", "ma_chi_nhanh", "ma_tk"}))
@Data
public class HuyDongKyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ky", nullable = false, length = 7)
    private String ky;

    @Column(name = "ma_chi_nhanh", nullable = false, length = 5)
    private String maChiNhanh;

    @Column(name = "ma_tk", nullable = false, length = 20)
    private String maTk;

    @Column(name = "ten_tk")
    private String tenTk;

    @Column(name = "tu_ngay")
    private LocalDate tuNgay;

    @Column(name = "den_ngay")
    private LocalDate denNgay;

    @Column(name = "du_dau_ky", nullable = false)
    private long duDauKy;

    @Column(name = "ps_no", nullable = false)
    private long psNo;

    @Column(name = "ps_co", nullable = false)
    private long psCo;

    @Column(name = "du_cuoi_ky", nullable = false)
    private long duCuoiKy;

    @Column(name = "tang_rong", nullable = false)
    private long tangRong;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
