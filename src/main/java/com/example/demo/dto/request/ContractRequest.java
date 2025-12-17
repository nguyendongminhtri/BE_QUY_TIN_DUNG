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
    private List<FileMetadataDto> fileAvatarUrls;
    private TableRequest tableRequest;
}
