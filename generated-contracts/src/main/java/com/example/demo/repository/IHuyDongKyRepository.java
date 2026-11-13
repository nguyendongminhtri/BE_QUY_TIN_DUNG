package com.example.demo.repository;

import com.example.demo.model.HuyDongKyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHuyDongKyRepository extends JpaRepository<HuyDongKyEntity, Long> {
    @Modifying
    @Query("delete from HuyDongKyEntity h where h.ky = :ky and h.maChiNhanh = :maChiNhanh")
    int deleteByKyAndChiNhanh(@Param("ky") String ky, @Param("maChiNhanh") String maChiNhanh);

    // Dùng cho màn hình KPI huy động: lấy số liệu của một tháng, mọi chi nhánh
    List<HuyDongKyEntity> findByKyOrderByMaChiNhanhAsc(String ky);

    List<HuyDongKyEntity> findByKyAndMaChiNhanh(String ky, String maChiNhanh);
}
