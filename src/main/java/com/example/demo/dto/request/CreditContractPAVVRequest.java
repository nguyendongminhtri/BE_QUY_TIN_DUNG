package com.example.demo.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreditContractPAVVRequest {
    private String name;
    private String address;
    private String reason;
    private Boolean checkAddress;
    private String tongVon;
    private String tongVonLuuDong;
    private String vonTuCo;
    private String vonKhac;
    private Integer reLoanSequence;
    // 👉 thêm trường vayLai
    private Boolean vayLai;
    private Boolean giaiNganHM;
    private Double heSoVonKhac;
    private String nguoiChuyenKhoan;
    private String loaiPhuongAn;
    private String  duNoTruoc;
    private String  soTienVayLanNay;
    private String  soGiaiNgan;
    private LocalDate ngayGiaiNgan;
    private String ngayHDTDCu;
    private String soHDTDCu;
    private String ngayThuLaiHM;
}
