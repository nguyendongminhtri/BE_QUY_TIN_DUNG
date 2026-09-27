package com.example.demo.dto.request;
import lombok.Data;
import lombok.ToString;
import java.util.List;

@Data
@ToString
public class ContractRequest {
    private Long id;
    private String contractDate;
    private String ngayTheChap;
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
    private String choVay;
    // Người thân
    private String gtnt;
    private String tenNguoiThan;
    private String namSinhNguoiThan;
    private String cccdNguoiThan;
    private String ngayCapCCCDNguoiThan;
    private String noiCapCCCDNguoiThan;
    private String diaChiThuongTruNguoiThan;
    private String quanHe;

    // Thông tin hợp đồng vay
    private String tienSo;
    private String tienChu;
    private String muchDichVay;
    private String hanMuc;
    private String laiSuat;
    private String soBBXetDuyetChoVay;
    private String loaiVay;
    private String soHopDongTD;
    private String ngayKetThucKyHanVay;
    private String thoiHanVay;
    private TableRequest tableRequest;
    // Các checkbox chung
    private Boolean checkOption;

    // Avatar files
    private List<FileMetadataDto> fileAvatarUrls;

    // Các bảng chung của hợp đồng
    private TableRequest hanMucTable;
    private TableRequest chiPhiTable;
    private TableRequest thuNhapDuKienTable;
    private TableRequest phuLucHanMucTable;

    // Thông tin phòng giao dịch
    private String phongGiaoDich;
    private String diaChiPhongGiaoDich;

    // Liên quan đến bảo lãnh
    private Boolean checkHopDongBaoLanh;

    // Tổng tài sản bảo đảm
//    private String tongTaiSanBD;
//    private String tongTaiSanBDChu;

    // Danh sách tài sản (TSBD)
    private List<CreditContractTSBDRequest> taiSanArray;

    // Phương án vay vốn
    private CreditContractPAVVRequest pavvRequest;
}
