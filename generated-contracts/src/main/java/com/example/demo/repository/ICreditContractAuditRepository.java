package com.example.demo.repository;

import com.example.demo.model.AuditAction;
import com.example.demo.model.CreditContractAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ICreditContractAuditRepository  extends JpaRepository<CreditContractAuditEntity, Long> {
    @Query("""
    SELECT a.user.id AS userId,
           a.user.username AS username,
           SUM(CASE WHEN a.action = com.example.demo.model.AuditAction.CREATE THEN 1 ELSE 0 END) AS created,
           SUM(CASE WHEN a.action = com.example.demo.model.AuditAction.UPDATE THEN 1 ELSE 0 END) AS updateActions,
           SUM(CASE WHEN a.action = com.example.demo.model.AuditAction.DELETE THEN 1 ELSE 0 END) AS deleted
    FROM CreditContractAuditEntity a
    WHERE a.actionAt >= :from AND a.actionAt < :to
    GROUP BY a.user.id, a.user.username
    ORDER BY a.user.username
    """)
    List<UserContractStat> statsByUser(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
    SELECT a.user.id AS userId, COUNT(DISTINCT a.contractId) AS updatedContracts
    FROM CreditContractAuditEntity a
    WHERE a.action = com.example.demo.model.AuditAction.UPDATE
      AND a.actionAt >= :from AND a.actionAt < :to
    GROUP BY a.user.id
    """)
    List<UpdatedContractCount> countUpdatedContracts(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    interface UserContractStat {
        Long getUserId();
        String getUsername();
        Long getCreated();
        Long getUpdateActions();
        Long getDeleted();
    }

    interface UpdatedContractCount {
        Long getUserId();
        Long getUpdatedContracts();
    }
}
