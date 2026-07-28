package com.example.demo.dto.request;

import lombok.Data;

@Data
public class CreditContractTSBDRequest {
    private Boolean checkTaiSanGanLienVoiDat;
    private String dienTichTS;
    private String ketCauXayDung;
    private String loaiNha;
    // Người đứng tên bìa đỏ 1
    private Boolean checkCMNDDungTenBiaDo1;
    private String cmndDungTenBiaDo1;
    private Boolean checkNgayCapCCCDTruocDayDungTenBiaDo1;
    private String ngayCapCCCDTruocDayDungTenBiaDo1;

    // Người đứng tên bìa đỏ 2
    private Boolean checkCMNDDungTenBiaDo2;
    private String cmndDungTenBiaDo2;
    private Boolean checkNgayCapCCCDTruocDayDungTenBiaDo2;
    private String ngayCapCCCDTruocDayDungTenBiaDo2;
}
