package com.example.demo.repository;

import com.example.demo.model.CreditContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICreditContractRepository extends JpaRepository<CreditContractEntity, Long> {
}
