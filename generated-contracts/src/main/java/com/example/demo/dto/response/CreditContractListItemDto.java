package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record CreditContractListItemDto(Long id,
                                        String tenKhachHang,
                                        String cccdKhachHang,
                                        String namSinhKhachHang,
                                        String phoneKhachHang,
                                        String soTheThanhVienKhachHang,
                                        String loaiVay,
                                        String soHopDongTD,
                                        String muchDichVay,
                                        Boolean tatToan,
                                        Boolean vayLai,
                                        Integer reLoanSequence,
                                        String createdByUsername,
                                        LocalDateTime createdAt) {
}
