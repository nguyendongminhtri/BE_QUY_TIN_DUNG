package com.example.demo.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreditContractTSBDRequest {
    private Long id;

    // Thông tin hợp đồng thế chấp
    private String soHopDongTheChapQSDD;
    private LocalDate ngayTheChap;
    private String serial;
    private String noiCapSo;
    private String ngayCapSo;
    private String noiDungVaoSo;

    // Thông tin thửa đất
    private String soThuaDat;
    private String soBanDo;
    private String diaChiThuaDat;
    private String dienTichDatSo;
    private String dienTichDatChu;
    private String thoiHanSuDung;
    private String hinhThucSuDung;

    // Nội dung khác
    private String muchDichSuDung;
    private String loaiDat;
    private String noiDungThoaThuan;
    private String nguonGocSuDung;
    private String ghiChu;

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
    private String diaChiThuongTruDungTenBiaDo1;
    private String phoneDungTenBiaDo1;

    // Người đứng tên bìa đỏ 2
    private String dungTenBiaDo2;
    private String gioiTinhDungTenBiaDo2;
    private String namSinhDungTenBiaDo2;
    private String cccdDungTenBiaDo2;
    private String ngayCapCCCDDungTenBiaDo2;
    private String noiCapCCCDDungTenBiaDo2;
    private String diaChiThuongTruDungTenBiaDo2;
    private Boolean checkTaiSanGanLienVoiDat;
    private String dienTichTS;
    private String ketCauXayDung;
    private String loaiNha;
    private String noiDungNgoaiBia;

    // Các checkbox CMND/CCCD
    private String cmndDungTenBiaDo1;
    private String ngayCapCCCDTruocDayDungTenBiaDo1;
    private Boolean checkCMNDDungTenBiaDo1;
    private Boolean checkNgayCapCCCDTruocDayDungTenBiaDo1;
    private Boolean checkDiaChiThuongTruDungTenBiaDo1;
    private Boolean checkChiMangTenNguoi1;

    private String cmndDungTenBiaDo2;
    private String ngayCapCCCDTruocDayDungTenBiaDo2;
    private Boolean checkCMNDDungTenBiaDo2;
    private Boolean checkNgayCapCCCDTruocDayDungTenBiaDo2;
    private Boolean checkDiaChiThuongTruDungTenBiaDo2;
    private Boolean checkChiMangTenNguoi2;
    private Boolean checkDongSoHuu;

    // Thông tin khác
    private Boolean checkNguoiMangTenBiaDo;
    private String nguoiMangTen;
    private String landItems;
    private String tongTaiSanBD;
    private String tongTaiSanBDChu;

    // Các bảng riêng cho từng tài sản
    private TableRequest table1;
    private TableRequest table2;
    private TableRequest table3;
}
