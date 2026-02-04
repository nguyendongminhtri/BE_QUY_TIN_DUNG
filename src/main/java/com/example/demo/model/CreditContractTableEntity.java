package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "credit_contract_table")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditContractTableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tableName;
    @Column(columnDefinition = "LONGTEXT")
    private String tableJson;
    @ManyToOne
    @JoinColumn(name = "contract_id")
    private CreditContractEntity creditContract;
}
