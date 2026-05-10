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
}
