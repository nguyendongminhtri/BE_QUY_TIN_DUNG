package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "credit_contract_pavv")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditContractPAVVEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Khóa ngoại liên kết về CreditContractEntity
    @OneToOne
    @JoinColumn(name = "credit_contract_id")
    private CreditContractEntity creditContract;
    private String name;
    private String address;
    private String reason;
    private Boolean checkAddress;
}
