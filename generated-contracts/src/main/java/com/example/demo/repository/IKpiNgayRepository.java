package com.example.demo.repository;

import com.example.demo.model.KpiNgayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface IKpiNgayRepository extends JpaRepository<KpiNgayEntity, String> {
    @Modifying
    @Query("delete from KpiNgayEntity k where k.ngay between :tu and :den")
    void xoaTheoKhoang(@Param("tu") LocalDate tu, @Param("den") LocalDate den);
}
