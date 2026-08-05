package com.example.demo.repository;

import com.example.demo.model.CreditContractPAVVEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICreditContractPAVVRepository extends JpaRepository<CreditContractPAVVEntity, Long> {
    boolean existsByReLoanSequence(Integer reLoanSequence);
}
