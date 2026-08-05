package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "credit_contract_tsbd")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditContractTSBDEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Khóa ngoại liên kết về CreditContractEntity
    @OneToOne
    @JoinColumn(name = "credit_contract_id")
    private CreditContractEntity creditContract;
    // Các trường bổ sung
    private Boolean checkTaiSanGanLienVoiDat;
    private String dienTichTS;
    private String ketCauXayDung;
    private String loaiNha;
    // Người đứng tên bìa đỏ 1
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
}
