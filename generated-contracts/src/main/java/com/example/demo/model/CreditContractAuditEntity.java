package com.example.demo.model;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_contract_audit",
        indexes = {
                @Index(name = "idx_audit_user_time", columnList = "user_id, actionAt"),
                @Index(name = "idx_audit_contract", columnList = "contractId")
        })
@Data
@NoArgsConstructor
public class CreditContractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contractId;        // không dùng FK để xóa hợp đồng vẫn giữ log

    @ManyToOne
    private User user;              // người thực hiện

    @Enumerated(EnumType.STRING)
    private AuditAction action;

    private LocalDateTime actionAt;
}