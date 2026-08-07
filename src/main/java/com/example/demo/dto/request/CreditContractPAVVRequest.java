package com.example.demo.dto.request;

import lombok.Data;

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
    private Double heSoVonTuCo;
    private String nguoiChuyenKhoan;
    private String loaiPhuongAn;
}
