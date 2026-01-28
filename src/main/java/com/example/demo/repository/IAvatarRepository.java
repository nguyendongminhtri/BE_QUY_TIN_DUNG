package com.example.demo.repository;

import com.example.demo.model.AvatarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAvatarRepository extends JpaRepository<AvatarEntity, Long> {
//    List<AvatarEntity> findAllByCreditContractID(Long creditContractID);
}
