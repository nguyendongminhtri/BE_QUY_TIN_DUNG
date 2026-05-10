package com.example.demo.dto.request;

import lombok.Data;

@Data
public class CreditContractTSBDRequest {
    private Boolean checkTaiSanGanLienVoiDat;
    private String dienTichTS;
    private String ketCauXayDung;
    private String loaiNha;
}
