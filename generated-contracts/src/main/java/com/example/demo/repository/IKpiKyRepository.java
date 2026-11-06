package com.example.demo.repository;

import com.example.demo.model.KpiKyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IKpiKyRepository extends JpaRepository<KpiKyEntity, String> {
    @Transactional
    @Modifying
    @Query("delete from KpiKyEntity k where k.ky = :ky")
    void xoaTheoKy(@Param("ky") String ky);

    List<KpiKyEntity> findByKyOrderBySoLuotDesc(String ky);
}
