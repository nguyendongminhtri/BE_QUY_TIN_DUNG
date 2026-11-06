package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "kpi_ngay", indexes = @Index(name = "ix_kpi_ngay", columnList = "ngay"))
public class KpiNgayEntity {
    @Id
    @Column(length = 80)
    private String id;                    // "2026-11-06|KIEN93"
    private LocalDate ngay;
    @Column(name = "nguoi_tao", length = 50)
    private String nguoiTao;
    @Column(name = "so_luot")
    private Integer soLuot;
    @Column(name = "so_khach")
    private Integer soKhach;
    @Column(name = "synced_at")
    private LocalDateTime syncedAt;
}
