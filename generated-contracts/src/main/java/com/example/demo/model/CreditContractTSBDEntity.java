package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "credit_contract_tsbd")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditContractTSBDEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Khóa ngoại liên kết về CreditContractEntity
    @ManyToOne
    @JoinColumn(name = "credit_contract_id")
    private CreditContractEntity creditContract;
    private String noiDungNgoaiBia;
    // Các trường bổ sung
    private Boolean checkTaiSanGanLienVoiDat;
    private String dienTichTS;
    private String ketCauXayDung;
    private String loaiNha;
    // Người đứng tên bìa đỏ 1
    private String phoneDungTenBiaDo1;
    private Boolean checkCMNDDungTenBiaDo1;
    private String cmndDungTenBiaDo1;
    private Boolean checkNgayCapCCCDTruocDayDungTenBiaDo1;
    private String ngayCapCCCDTruocDayDungTenBiaDo1;
    private Boolean checkDiaChiThuongTruDungTenBiaDo1;
    private String diaChiThuongTruDungTenBiaDo1;

    // Người đứng tên bìa đỏ 2
    private Boolean checkCMNDDungTenBiaDo2;
    private String cmndDungTenBiaDo2;
    private Boolean checkNgayCapCCCDTruocDayDungTenBiaDo2;
    private String ngayCapCCCDTruocDayDungTenBiaDo2;
    private Boolean checkDiaChiThuongTruDungTenBiaDo2;
    private String diaChiThuongTruDungTenBiaDo2;

    //Xử lý HĐ HM Vay lại
    private String soHDTDCu;
    private LocalDate ngayHDTDCu;
    private Boolean checkChiMangTenNguoi2;
    private Boolean checkChiMangTenNguoi1;
    // Thông tin hợp đồng thế chấp
    private String soHopDongTheChapQSDD;
    private LocalDate ngayTheChap;
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
    private String loaiDat;
    // Các checkbox riêng cho từng tài sản
    private Boolean checkMucDich;
    private Boolean checkLoaiDat;
    private Boolean checkNguonGocSuDung;
    private Boolean checkGhiChu;
    private Boolean checkHopDongBaoLanh;

    // Người đứng tên bìa đỏ 1
    private String dungTenBiaDo1;
    private String gioiTinhDungTenBiaDo1;
    private String namSinhDungTenBiaDo1;
    private String cccdDungTenBiaDo1;
    private String ngayCapCCCDDungTenBiaDo1;
    private String noiCapCCCDDungTenBiaDo1;


    // Người đứng tên bìa đỏ 2
    private String dungTenBiaDo2;
    private String gioiTinhDungTenBiaDo2;
    private String namSinhDungTenBiaDo2;
    private String cccdDungTenBiaDo2;
    private String ngayCapCCCDDungTenBiaDo2;
    private String noiCapCCCDDungTenBiaDo2;
    private Boolean checkDongSoHuu;

    // Thông tin khác
    private Boolean checkNguoiMangTenBiaDo;
    private String nguoiMangTen;
    @Lob
    private String landItems;
    private String tongTaiSanBD;
    private String tongTaiSanBDChu;
    @OneToMany(mappedBy = "tsbd", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CreditContractTableEntity> tables = new ArrayList<>();
}
