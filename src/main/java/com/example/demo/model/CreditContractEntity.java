package com.example.demo.model;

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
    private String diaChiThuongTruKhachHang;
    private String gtnt;
    private String tenNguoiThan;
    private String namSinhNguoiThan;
    private String cccdNguoiThan;
    private String ngayCapCCCDNguoiThan;
    private String diaChiThuongTruNguoiThan;
    private String quanHe;
    private String tienSo;
    private String tienChu;
    private String muchDichVay;
    private String hanMuc;
    private String laiSuat;
    private String soHopDongTheChapQSDD;

    //thong tin bia do
    private String serial;
    private String noiCapSo;
    private String ngayCapSo;
    private String noiDungVaoSo;
    private String soThuaDat;
    private String soBanDo;
    private String diaChiThuaDat;
    private String dienTichDatSo;
    private String dienTichDatChu;
    private String hinhThucSuDung;
    private String muchDichSuDung;
    private String thoiHanSuDung;
    private String soBienBanDinhGia;
    private String noiDungThoaThuan;
    private String nguonGocSuDung;
    private String ghiChu;


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
    private List<AvatarEntity> avatars = new ArrayList<>();

}
