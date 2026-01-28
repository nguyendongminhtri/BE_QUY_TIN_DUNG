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
    private Boolean checkMucDichSuDung;
    private String muchDichSuDung;
    private String thoiHanSuDung;
    private String soBienBanDinhGia;
    private String noiDungThoaThuan;
    private String nguonGocSuDung;
    private String ghiChu;

    private String choVay;
    private String loaiVay;
    // 👉 Thêm trường checkOption
    private Boolean checkOption;
    private Boolean checkGhiChu;
    private Boolean checkNguonGocSuDung;
    private String loaiDat;
    private Boolean checkLoaiDat;
    @Column(columnDefinition = "TEXT")
    private String tableJson;
    private String soHopDongTD;
    private String ngayKetThucKyHanVay;
    //Người đứng tên bìa ỏ 1
    private String dungTenBiaDo1;
    private String gioiTinhDungTenBiaDo1;
    private String namSinhDungTenBiaDo1;
    private String phoneDungTenBiaDo1;
    private String cccdDungTenBiaDo1;
    private String ngayCapCCCDDungTenBiaDo1;
    @Column(columnDefinition = "TEXT")
    private String noiCapCCCDDungTenBiaDo1;
    private String diaChiThuongTruDungTenBiaDo1;

    private Boolean checkNguoiDungTenBiaDo2;
    private String dungTenBiaDo2;
    private String gioiTinhDungTenBiaDo2;
    private String namSinhDungTenBiaDo2;
    private String cccdDungTenBiaDo2;
    @Column(columnDefinition = "TEXT")
    private String noiCapCCCDDungTenBiaDo2;
    private String ngayCapCCCDDungTenBiaDo2;
    private String diaChiThuongTruDungTenBiaDo2;
    @Column(columnDefinition = "TEXT")
    private String landItems;
    private String thoiHanVay;
    private Boolean checkNhaCoDinh;
    private String nhaCoDinh;
    private String tongTaiSanBD;
    private String tongTaiSanBDChu;
    private String phongGiaoDich;
    private String diaChiPhongGiaoDich;
    private String benA;
    private Boolean checkHopDongBaoLanh;
    private Boolean checkNguoiMangTenBiaDo;
    @Column(columnDefinition = "TEXT")
    private String nguoiMangTen;

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
    private List<AvatarEntity> avatars = new ArrayList();

}
