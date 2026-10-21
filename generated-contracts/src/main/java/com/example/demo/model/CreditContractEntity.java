package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "credit_contract")
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CreditContractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate contractDate;
    private String nguoiDaiDien;
    private String gtkh;
    private String tenKhachHang;
    private String namSinhKhachHang;
    private String phoneKhachHang;
    private String soTheThanhVienKhachHang;
    private String cccdKhachHang;
    private String ngayCapCCCDKhachHang;
    private String noiCapCCCDKhachHang;
    private String diaChiThuongTruKhachHang;
    private String gtnt;
    private String tenNguoiThan;
    private String namSinhNguoiThan;
    private String cccdNguoiThan;
    private String ngayCapCCCDNguoiThan;
    @Column(columnDefinition = "TEXT")
    private String noiCapCCCDNguoiThan;
    private String diaChiThuongTruNguoiThan;
    private String quanHe;
    private String tienSo;
    private String tienChu;
    private String muchDichVay;
    private String hanMuc;
    private String laiSuat;
    private Boolean tatToan;

    private String choVay;
    private String loaiVay;
    private Boolean checkOption;
    @Column(columnDefinition = "TEXT")
    private String tableJson;
    private String soHopDongTD;
    private String ngayKetThucKyHanVay;

    private Boolean checkNguoiDungTenBiaDo2;
    private String thoiHanVay;
    private Boolean checkNhaCoDinh;
    private String nhaCoDinh;
    private String phongGiaoDich;
    private String diaChiPhongGiaoDich;
    @Column(columnDefinition = "TEXT")
    private String soBBXetDuyetChoVay;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @ManyToOne
    User user;
    @OneToMany(mappedBy = "creditContract", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AvatarEntity> avatars = new ArrayList<>();
    @OneToMany(mappedBy = "creditContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CreditContractTableEntity> tables = new ArrayList<>();
    @OneToMany(mappedBy = "creditContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CreditContractTSBDEntity> contractTSBDs = new ArrayList<>();
    @OneToOne(mappedBy = "creditContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private CreditContractPAVVEntity contractPAVV;
}