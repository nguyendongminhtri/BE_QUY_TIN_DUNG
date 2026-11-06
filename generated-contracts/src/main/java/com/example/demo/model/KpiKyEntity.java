package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "kpi_ky", indexes = {
        @Index(name = "ix_kpi_ky", columnList = "ky")
})
public class KpiKyEntity {
    @Id
    @Column(name = "id", length = 80)
    private String id;                       // "2026-10|KIEN93"

    @Column(name = "ky", length = 7)
    private String ky;                       // "2026-10"

    private LocalDate tu;
    private LocalDate den;

    @Column(name = "nguoi_tao", length = 50)
    private String nguoiTao;

    @Column(name = "so_luot")
    private Integer soLuot;

    @Column(name = "so_khach")
    private Integer soKhach;

    @Column(name = "synced_at")
    private LocalDateTime syncedAt;
}
