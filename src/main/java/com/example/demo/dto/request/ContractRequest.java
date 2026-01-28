package com.example.demo.dto.request;

import lombok.Data;
import lombok.ToString;
import java.util.List;

@Data
@ToString
public class ContractRequest {
    private String contractDate;
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
    private String noiCapCCCDNguoiThan;
    private String diaChiThuongTruNguoiThan;
    private String quanHe;

    private String tienSo;
    private String tienChu;
    private String muchDichVay;
    private String hanMuc;
    private String laiSuat;
    private String soHopDongTheChapQSDD;

    //Thong tin Bia Do
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
    private String choVay;
    private String loaiVay;
    private String soHopDongTD;
    // 👉 Thêm trường checkOption
    private Boolean checkOption;
    private Boolean checkGhiChu;
    private Boolean checkNguonGocSuDung;
    private List<FileMetadataDto> fileAvatarUrls;
    private TableRequest tableRequest;
    private String ngayKetThucKyHanVay;
    private String dungTenBiaDo1;
    private Boolean checkNguoiDungTenBiaDo2;
    private String dungTenBiaDo2;
    private String landItems;
    private String thoiHanVay;
    private String nhaCoDinh;
    private Boolean checkNhaCoDinh;
    private String tongTaiSanBD;
    private String tongTaiSanBDChu;
    private String loaiDat;
    private Boolean checkLoaiDat;
    private Boolean checkMucDich;
    private String gioiTinhDungTenBiaDo1;
    private String namSinhDungTenBiaDo1;
    private String phoneDungTenBiaDo1;
    private String cccdDungTenBiaDo1;
    private String noiCapCCCDDungTenBiaDo1;
    private String ngayCapCCCDDungTenBiaDo1;
    private String diaChiThuongTruDungTenBiaDo1;
    private String gioiTinhDungTenBiaDo2;
    private String namSinhDungTenBiaDo2;
    private String cccdDungTenBiaDo2;
    private String noiCapCCCDDungTenBiaDo2;
    private String ngayCapCCCDDungTenBiaDo2;
    private String diaChiThuongTruDungTenBiaDo2;
    private String phongGiaoDich;
    private String diaChiPhongGiaoDich;
    private String benA;
    private Boolean checkNguoiMangTenBiaDo;
    private String nguoiMangTen;
    private Boolean checkHopDongBaoLanh;
}
