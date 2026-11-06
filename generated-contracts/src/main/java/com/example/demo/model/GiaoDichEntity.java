package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "giao_dich", indexes = {
        @Index(name = "ix_gd_ngay", columnList = "ngay"),
        @Index(name = "ix_gd_nguoi_tao", columnList = "nguoi_tao,ngay_tao")
})
public class GiaoDichEntity {

    @Id
    @Column(name = "so_ct", length = 50)
    private String soCt; // khóa chính

    private LocalDate ngay;

    @Column(name = "dien_giai", columnDefinition = "TEXT")
    private String dienGiai;

    @Column(name = "loai_phieu", length = 100)
    private String loaiPhieu;

    @Column(name = "nghiep_vu", length = 100)
    private String nghiepVu;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Column(name = "nguoi_tao", length = 50)
    private String nguoiTao;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "nguoi_duyet", length = 50)
    private String nguoiDuyet;

    @Column(name = "ngay_duyet")
    private LocalDateTime ngayDuyet;

    @Column(name = "ma_kh", length = 50)
    private String maKh;

    @Column(name = "ten_kh", length = 200)
    private String tenKh;

    @Column(name = "synced_at")
    private LocalDateTime syncedAt;
}