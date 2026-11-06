package com.example.demo.repository;

import com.example.demo.dto.response.CreditContractListItemDto;
import com.example.demo.model.CreditContractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICreditContractRepository extends JpaRepository<CreditContractEntity, Long> {
    Optional<CreditContractEntity> findFirstBySoHopDongTDAndContractPAVV_ReLoanSequence(
            String soHopDongTD, Integer reLoanSequence);
    @Query(value = """
    SELECT new com.example.demo.dto.response.CreditContractListItemDto(
        c.id, c.tenKhachHang, c.cccdKhachHang, c.namSinhKhachHang, c.phoneKhachHang,
        c.soTheThanhVienKhachHang, c.loaiVay, c.soHopDongTD, c.muchDichVay, c.tatToan,
        p.vayLai, p.reLoanSequence, u.username, c.createdAt)
    FROM CreditContractEntity c
    LEFT JOIN c.user u
    LEFT JOIN c.contractPAVV p
    WHERE :kw = ''
       OR LOWER(c.tenKhachHang) LIKE :pattern
       OR LOWER(c.cccdKhachHang) LIKE :pattern
       OR LOWER(c.phoneKhachHang) LIKE :pattern
       OR LOWER(c.soTheThanhVienKhachHang) LIKE :pattern
       OR LOWER(c.soHopDongTD) LIKE :pattern
       OR LOWER(c.namSinhKhachHang) LIKE :pattern
       OR LOWER(c.loaiVay) LIKE :pattern
       OR LOWER(c.muchDichVay) LIKE :pattern
       OR LOWER(u.username) LIKE :pattern
       OR EXISTS (SELECT 1 FROM CreditContractTSBDEntity t
                  WHERE t.creditContract = c
                    AND (LOWER(t.soHopDongTheChapQSDD) LIKE :pattern
                      OR LOWER(t.dungTenBiaDo1) LIKE :pattern
                      OR LOWER(t.dungTenBiaDo2) LIKE :pattern
                      OR LOWER(t.cccdDungTenBiaDo1) LIKE :pattern
                      OR LOWER(t.cccdDungTenBiaDo2) LIKE :pattern))
    ORDER BY c.id DESC
    """,
            countQuery = """
    SELECT COUNT(c)
    FROM CreditContractEntity c
    LEFT JOIN c.user u
    WHERE :kw = ''
       OR LOWER(c.tenKhachHang) LIKE :pattern
       OR LOWER(c.cccdKhachHang) LIKE :pattern
       OR LOWER(c.phoneKhachHang) LIKE :pattern
       OR LOWER(c.soTheThanhVienKhachHang) LIKE :pattern
       OR LOWER(c.soHopDongTD) LIKE :pattern
       OR LOWER(c.namSinhKhachHang) LIKE :pattern
       OR LOWER(c.loaiVay) LIKE :pattern
       OR LOWER(c.muchDichVay) LIKE :pattern
       OR LOWER(u.username) LIKE :pattern
       OR EXISTS (SELECT 1 FROM CreditContractTSBDEntity t
                  WHERE t.creditContract = c
                    AND (LOWER(t.soHopDongTheChapQSDD) LIKE :pattern
                      OR LOWER(t.dungTenBiaDo1) LIKE :pattern
                      OR LOWER(t.dungTenBiaDo2) LIKE :pattern
                      OR LOWER(t.cccdDungTenBiaDo1) LIKE :pattern
                      OR LOWER(t.cccdDungTenBiaDo2) LIKE :pattern))
    """)
    Page<CreditContractListItemDto> search(@Param("kw") String kw,
                                           @Param("pattern") String pattern,
                                           Pageable pageable);
}
