package com.example.demo.mapper;

import com.example.demo.dto.request.*;
import com.example.demo.model.*;
import com.example.demo.repository.IFileMetadataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ContractMapper {
    /**
     * Map dữ liệu từ ContractRequest sang CreditContractEntity
     */
    public void mapRequestToEntity(ContractRequest request,
                                   CreditContractEntity entity,
                                   User user,
                                   LocalDate date,
                                   LocalDate dateTC
                                   ) throws JsonProcessingException {
        entity.setUser(user);
        entity.setContractDate(date);
        entity.setNgayTheChap(dateTC);
//        entity.setNgayBaoDam(dateBD);
        entity.setNguoiDaiDien(request.getNguoiDaiDien());
        entity.setGtkh(request.getGtkh());
        entity.setTenKhachHang(request.getTenKhachHang());
        entity.setNamSinhKhachHang(request.getNamSinhKhachHang());
        entity.setPhoneKhachHang(request.getPhoneKhachHang());
        entity.setSoTheThanhVienKhachHang(request.getSoTheThanhVienKhachHang());
        entity.setCccdKhachHang(request.getCccdKhachHang());
        entity.setNgayCapCCCDKhachHang(request.getNgayCapCCCDKhachHang());
        entity.setDiaChiThuongTruKhachHang(request.getDiaChiThuongTruKhachHang());

        entity.setGtnt(request.getGtnt());
        entity.setTenNguoiThan(request.getTenNguoiThan());
        entity.setNamSinhNguoiThan(request.getNamSinhNguoiThan());
        entity.setCccdNguoiThan(request.getCccdNguoiThan());
        entity.setNgayCapCCCDNguoiThan(request.getNgayCapCCCDNguoiThan());
        entity.setDiaChiThuongTruNguoiThan(request.getDiaChiThuongTruNguoiThan());
        entity.setQuanHe(request.getQuanHe());
        entity.setTienSo(request.getTienSo());
        entity.setTienChu(request.getTienChu());
        entity.setMuchDichVay(request.getMuchDichVay());
        entity.setHanMuc(request.getHanMuc());
        entity.setLaiSuat(request.getLaiSuat());
        entity.setChoVay(request.getChoVay());
        entity.setLoaiVay(request.getLoaiVay());
        entity.setCheckOption(request.getCheckOption());
        entity.setSoHopDongTD(request.getSoHopDongTD());
        entity.setNgayKetThucKyHanVay(request.getNgayKetThucKyHanVay());
        entity.setThoiHanVay(request.getThoiHanVay());
//        entity.setTongTaiSanBD(request.getTongTaiSanBD());
//        entity.setTongTaiSanBDChu(request.getTongTaiSanBDChu());
        entity.setPhongGiaoDich(request.getPhongGiaoDich());
        entity.setDiaChiPhongGiaoDich(request.getDiaChiPhongGiaoDich());
        entity.setNoiCapCCCDKhachHang(request.getNoiCapCCCDKhachHang());
        entity.setNoiCapCCCDNguoiThan(request.getNoiCapCCCDNguoiThan());
        entity.setCheckHopDongBaoLanh(request.getCheckHopDongBaoLanh());
        entity.setSoBBXetDuyetChoVay(request.getSoBBXetDuyetChoVay());
        entity.getContractTSBDs().clear();
        if (request.getTaiSanArray() != null) {
            for (CreditContractTSBDRequest dto : request.getTaiSanArray()) {
                CreditContractTSBDEntity tsbd = new CreditContractTSBDEntity();
                tsbd.setCreditContract(entity);

                // Map các trường từ dto sang tsbd
                tsbd.setSoHopDongTheChapQSDD(dto.getSoHopDongTheChapQSDD());
                tsbd.setNgayTheChap(dto.getNgayTheChap());
                tsbd.setNoiDungNgoaiBia(dto.getNoiDungNgoaiBia());
                tsbd.setSerial(dto.getSerial());
                tsbd.setNoiCapSo(dto.getNoiCapSo());
                tsbd.setNgayCapSo(dto.getNgayCapSo());
                tsbd.setNoiDungVaoSo(dto.getNoiDungVaoSo());
                tsbd.setSoThuaDat(dto.getSoThuaDat());
                tsbd.setSoBanDo(dto.getSoBanDo());
                tsbd.setDiaChiThuaDat(dto.getDiaChiThuaDat());
                tsbd.setDienTichDatSo(dto.getDienTichDatSo());
                tsbd.setDienTichDatChu(dto.getDienTichDatChu());
                tsbd.setThoiHanSuDung(dto.getThoiHanSuDung());
                tsbd.setHinhThucSuDung(dto.getHinhThucSuDung());
                tsbd.setMuchDichSuDung(dto.getMuchDichSuDung());
                tsbd.setLoaiDat(dto.getLoaiDat());
                tsbd.setNoiDungThoaThuan(dto.getNoiDungThoaThuan());
                tsbd.setNguonGocSuDung(dto.getNguonGocSuDung());
                tsbd.setGhiChu(dto.getGhiChu());
                tsbd.setCheckMucDich(dto.getCheckMucDich());
                tsbd.setCheckLoaiDat(dto.getCheckLoaiDat());
                tsbd.setCheckNguonGocSuDung(dto.getCheckNguonGocSuDung());
                tsbd.setCheckGhiChu(dto.getCheckGhiChu());
                tsbd.setCheckHopDongBaoLanh(dto.getCheckHopDongBaoLanh());

                // Người đứng tên bìa đỏ 1
                tsbd.setDungTenBiaDo1(dto.getDungTenBiaDo1());
                tsbd.setGioiTinhDungTenBiaDo1(dto.getGioiTinhDungTenBiaDo1());
                tsbd.setNamSinhDungTenBiaDo1(dto.getNamSinhDungTenBiaDo1());
                tsbd.setCccdDungTenBiaDo1(dto.getCccdDungTenBiaDo1());
                tsbd.setNgayCapCCCDDungTenBiaDo1(dto.getNgayCapCCCDDungTenBiaDo1());
                tsbd.setNoiCapCCCDDungTenBiaDo1(dto.getNoiCapCCCDDungTenBiaDo1());
                tsbd.setDiaChiThuongTruDungTenBiaDo1(dto.getDiaChiThuongTruDungTenBiaDo1());

                // Người đứng tên bìa đỏ 2
                tsbd.setDungTenBiaDo2(dto.getDungTenBiaDo2());
                tsbd.setGioiTinhDungTenBiaDo2(dto.getGioiTinhDungTenBiaDo2());
                tsbd.setNamSinhDungTenBiaDo2(dto.getNamSinhDungTenBiaDo2());
                tsbd.setCccdDungTenBiaDo2(dto.getCccdDungTenBiaDo2());
                tsbd.setNgayCapCCCDDungTenBiaDo2(dto.getNgayCapCCCDDungTenBiaDo2());
                tsbd.setNoiCapCCCDDungTenBiaDo2(dto.getNoiCapCCCDDungTenBiaDo2());
                tsbd.setDiaChiThuongTruDungTenBiaDo2(dto.getDiaChiThuongTruDungTenBiaDo2());

                // Các checkbox CMND/CCCD
                tsbd.setCmndDungTenBiaDo1(dto.getCmndDungTenBiaDo1());
                tsbd.setNgayCapCCCDTruocDayDungTenBiaDo1(dto.getNgayCapCCCDTruocDayDungTenBiaDo1());
                tsbd.setCheckCMNDDungTenBiaDo1(dto.getCheckCMNDDungTenBiaDo1());
                tsbd.setCheckNgayCapCCCDTruocDayDungTenBiaDo1(dto.getCheckNgayCapCCCDTruocDayDungTenBiaDo1());
                tsbd.setCheckDiaChiThuongTruDungTenBiaDo1(dto.getCheckDiaChiThuongTruDungTenBiaDo1());
                tsbd.setCheckChiMangTenNguoi1(dto.getCheckChiMangTenNguoi1());

                tsbd.setCmndDungTenBiaDo2(dto.getCmndDungTenBiaDo2());
                tsbd.setNgayCapCCCDTruocDayDungTenBiaDo2(dto.getNgayCapCCCDTruocDayDungTenBiaDo2());
                tsbd.setCheckCMNDDungTenBiaDo2(dto.getCheckCMNDDungTenBiaDo2());
                tsbd.setCheckNgayCapCCCDTruocDayDungTenBiaDo2(dto.getCheckNgayCapCCCDTruocDayDungTenBiaDo2());
                tsbd.setCheckDiaChiThuongTruDungTenBiaDo2(dto.getCheckDiaChiThuongTruDungTenBiaDo2());
                tsbd.setCheckChiMangTenNguoi2(dto.getCheckChiMangTenNguoi2());

                // Thông tin khác
                tsbd.setCheckNguoiMangTenBiaDo(dto.getCheckNguoiMangTenBiaDo());
                tsbd.setNguoiMangTen(dto.getNguoiMangTen());
                tsbd.setLandItems(dto.getLandItems());
                tsbd.setTongTaiSanBD(dto.getTongTaiSanBD());
                tsbd.setTongTaiSanBDChu(dto.getTongTaiSanBDChu());
                tsbd.setDienTichTS(dto.getDienTichTS());
                tsbd.setLoaiNha(dto.getLoaiNha());
                tsbd.setKetCauXayDung(dto.getKetCauXayDung());
                tsbd.setCheckTaiSanGanLienVoiDat(dto.getCheckTaiSanGanLienVoiDat());
                tsbd.setCheckDongSoHuu(dto.getCheckDongSoHuu());
                // Map các bảng table1, table2, table3
                ObjectMapper mapper = new ObjectMapper();
                List<CreditContractTableEntity> tables = new ArrayList<>();
                if (dto.getTable1() != null) {
                    CreditContractTableEntity t1 = new CreditContractTableEntity();
                    t1.setTableName("table1");
                    t1.setTableJson(mapper.writeValueAsString(dto.getTable1()));
                    t1.setTsbd(tsbd);
                    tables.add(t1);
                }
                if (dto.getTable2() != null) {
                    CreditContractTableEntity t2 = new CreditContractTableEntity();
                    t2.setTableName("table2");
                    t2.setTableJson(mapper.writeValueAsString(dto.getTable2()));
                    t2.setTsbd(tsbd);
                    tables.add(t2);
                }
                if (dto.getTable3() != null) {
                    CreditContractTableEntity t3 = new CreditContractTableEntity();
                    t3.setTableName("table3");
                    t3.setTableJson(mapper.writeValueAsString(dto.getTable3()));
                    t3.setTsbd(tsbd);
                    tables.add(t3);
                }
                tsbd.setTables(tables);

                entity.getContractTSBDs().add(tsbd);
            }
        }

        if (request.getPavvRequest() != null) {
            CreditContractPAVVRequest dto = request.getPavvRequest();
            CreditContractPAVVEntity pavv = entity.getContractPAVV();

            if (pavv == null) {
                pavv = new CreditContractPAVVEntity();
                pavv.setCreditContract(entity); // liên kết ngược
            }

            pavv.setName(dto.getName());
            pavv.setAddress(dto.getAddress());
            pavv.setReason(dto.getReason());
            pavv.setCheckAddress(dto.getCheckAddress());
            pavv.setTongVon(dto.getTongVon());
            pavv.setVonTuCo(dto.getVonTuCo());
            pavv.setVonKhac(dto.getVonKhac());
            pavv.setReLoanSequence(dto.getReLoanSequence());
            pavv.setVayLai(dto.getVayLai());
            pavv.setHeSoVonKhac(dto.getHeSoVonKhac());
            pavv.setNguoiChuyenKhoan(dto.getNguoiChuyenKhoan());
            pavv.setLoaiPhuongAn(dto.getLoaiPhuongAn());
            pavv.setDuNoTruoc(dto.getDuNoTruoc());
            pavv.setSoTienVayLanNay(dto.getSoTienVayLanNay());
            pavv.setGiaiNganHM(dto.getGiaiNganHM());
            pavv.setSoGiaiNgan(dto.getSoGiaiNgan());
            pavv.setNgayGiaiNgan(dto.getNgayGiaiNgan());
            pavv.setNgayHDTDCu(dto.getNgayHDTDCu());
            pavv.setSoHDTDCu(dto.getSoHDTDCu());
            pavv.setNgayThuLaiHM(dto.getNgayThuLaiHM());
            entity.setContractPAVV(pavv); // liên kết xuôi
        }

        // Ánh xạ dữ liệu bảng sang JSON
        if (request.getTableRequest() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(request.getTableRequest());
                entity.setTableJson(json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Không thể convert TableRequest sang JSON", e);
            }
        }
//        CreditContractEntity contractEntity = new CreditContractEntity();
// map các field khác từ request...

        // Xóa các bảng cũ
        entity.getTables().clear();
        ObjectMapper mapper = new ObjectMapper();
// Thêm lại các bảng mới
        if (request.getHanMucTable() != null) {
            CreditContractTableEntity hm = new CreditContractTableEntity();
            hm.setTableName("hanMucTable");
            hm.setTableJson(mapper.writeValueAsString(request.getHanMucTable()));
            hm.setCreditContract(entity);
            entity.getTables().add(hm);
        }
        if (request.getChiPhiTable() != null) {
            CreditContractTableEntity hm = new CreditContractTableEntity();
            hm.setTableName("chiPhiTable");
            hm.setTableJson(mapper.writeValueAsString(request.getChiPhiTable()));
            hm.setCreditContract(entity);
            entity.getTables().add(hm);
        }
        if (request.getThuNhapDuKienTable() != null) {
            CreditContractTableEntity hm = new CreditContractTableEntity();
            hm.setTableName("thuNhapDuKienTable");
            hm.setTableJson(mapper.writeValueAsString(request.getThuNhapDuKienTable()));
            hm.setCreditContract(entity);
            entity.getTables().add(hm);
        }
        if (request.getThuNhapDuKienTable() != null) {
            CreditContractTableEntity hm = new CreditContractTableEntity();
            hm.setTableName("phuLucHanMucTable");
            hm.setTableJson(mapper.writeValueAsString(request.getPhuLucHanMucTable()));
            hm.setCreditContract(entity);
            entity.getTables().add(hm);
        }
    }

    public ContractRequest mapEntityToRequest(CreditContractEntity entity) throws JsonProcessingException {
        ContractRequest request = new ContractRequest();
        ObjectMapper mapper = new ObjectMapper();
        if (entity.getAvatars() != null && !entity.getAvatars().isEmpty()) {
            List<FileMetadataDto> avatarDtos = new ArrayList<>();
            for (AvatarEntity avatar : entity.getAvatars()) {
                FileMetadataDto dto = new FileMetadataDto();
                dto.setFileName(avatar.getFileName());
                dto.setContentType(avatar.getContentType());
                // Sử dụng URL public thay vì đường dẫn vật lý
                dto.setFileUrl(avatar.getFileUrl());
                avatarDtos.add(dto);
            }
            request.setFileAvatarUrls(avatarDtos);
        }
        // Thay vì if (entity.getContractTSBD() != null) { ... }
        if (entity.getContractTSBDs() != null && !entity.getContractTSBDs().isEmpty()) {
            List<CreditContractTSBDRequest> tsbdRequests = new ArrayList<>();
            for (CreditContractTSBDEntity tsbd : entity.getContractTSBDs()) {
                CreditContractTSBDRequest dto = new CreditContractTSBDRequest();

                // Map các trường từ entity sang dto
                dto.setSoHopDongTheChapQSDD(tsbd.getSoHopDongTheChapQSDD());
                dto.setNgayTheChap(tsbd.getNgayTheChap());
                dto.setSerial(tsbd.getSerial());
                dto.setNoiCapSo(tsbd.getNoiCapSo());
                dto.setNgayCapSo(tsbd.getNgayCapSo());
                dto.setNoiDungVaoSo(tsbd.getNoiDungVaoSo());
                dto.setSoThuaDat(tsbd.getSoThuaDat());
                dto.setSoBanDo(tsbd.getSoBanDo());
                dto.setDiaChiThuaDat(tsbd.getDiaChiThuaDat());
                dto.setDienTichDatSo(tsbd.getDienTichDatSo());
                dto.setDienTichDatChu(tsbd.getDienTichDatChu());
                dto.setThoiHanSuDung(tsbd.getThoiHanSuDung());
                dto.setHinhThucSuDung(tsbd.getHinhThucSuDung());
                dto.setMuchDichSuDung(tsbd.getMuchDichSuDung());
                dto.setLoaiDat(tsbd.getLoaiDat());
                dto.setNoiDungThoaThuan(tsbd.getNoiDungThoaThuan());
                dto.setNguonGocSuDung(tsbd.getNguonGocSuDung());
                dto.setGhiChu(tsbd.getGhiChu());
                dto.setCheckMucDich(tsbd.getCheckMucDich());
                dto.setCheckLoaiDat(tsbd.getCheckLoaiDat());
                dto.setCheckNguonGocSuDung(tsbd.getCheckNguonGocSuDung());
                dto.setCheckGhiChu(tsbd.getCheckGhiChu());
                dto.setCheckHopDongBaoLanh(tsbd.getCheckHopDongBaoLanh());

                // Người đứng tên bìa đỏ 1
                dto.setDungTenBiaDo1(tsbd.getDungTenBiaDo1());
                dto.setGioiTinhDungTenBiaDo1(tsbd.getGioiTinhDungTenBiaDo1());
                dto.setNamSinhDungTenBiaDo1(tsbd.getNamSinhDungTenBiaDo1());
                dto.setCccdDungTenBiaDo1(tsbd.getCccdDungTenBiaDo1());
                dto.setNgayCapCCCDDungTenBiaDo1(tsbd.getNgayCapCCCDDungTenBiaDo1());
                dto.setNoiCapCCCDDungTenBiaDo1(tsbd.getNoiCapCCCDDungTenBiaDo1());
                dto.setDiaChiThuongTruDungTenBiaDo1(tsbd.getDiaChiThuongTruDungTenBiaDo1());

                // Người đứng tên bìa đỏ 2
                dto.setDungTenBiaDo2(tsbd.getDungTenBiaDo2());
                dto.setGioiTinhDungTenBiaDo2(tsbd.getGioiTinhDungTenBiaDo2());
                dto.setNamSinhDungTenBiaDo2(tsbd.getNamSinhDungTenBiaDo2());
                dto.setCccdDungTenBiaDo2(tsbd.getCccdDungTenBiaDo2());
                dto.setNgayCapCCCDDungTenBiaDo2(tsbd.getNgayCapCCCDDungTenBiaDo2());
                dto.setNoiCapCCCDDungTenBiaDo2(tsbd.getNoiCapCCCDDungTenBiaDo2());
                dto.setDiaChiThuongTruDungTenBiaDo2(tsbd.getDiaChiThuongTruDungTenBiaDo2());

                // Các checkbox CMND/CCCD
                dto.setCmndDungTenBiaDo1(tsbd.getCmndDungTenBiaDo1());
                dto.setNgayCapCCCDTruocDayDungTenBiaDo1(tsbd.getNgayCapCCCDTruocDayDungTenBiaDo1());
                dto.setCheckCMNDDungTenBiaDo1(tsbd.getCheckCMNDDungTenBiaDo1());
                dto.setCheckNgayCapCCCDTruocDayDungTenBiaDo1(tsbd.getCheckNgayCapCCCDTruocDayDungTenBiaDo1());
                dto.setCheckDiaChiThuongTruDungTenBiaDo1(tsbd.getCheckDiaChiThuongTruDungTenBiaDo1());
                dto.setCheckChiMangTenNguoi1(tsbd.getCheckChiMangTenNguoi1());

                dto.setCmndDungTenBiaDo2(tsbd.getCmndDungTenBiaDo2());
                dto.setNgayCapCCCDTruocDayDungTenBiaDo2(tsbd.getNgayCapCCCDTruocDayDungTenBiaDo2());
                dto.setCheckCMNDDungTenBiaDo2(tsbd.getCheckCMNDDungTenBiaDo2());
                dto.setCheckNgayCapCCCDTruocDayDungTenBiaDo2(tsbd.getCheckNgayCapCCCDTruocDayDungTenBiaDo2());
                dto.setCheckDiaChiThuongTruDungTenBiaDo2(tsbd.getCheckDiaChiThuongTruDungTenBiaDo2());
                dto.setCheckChiMangTenNguoi2(tsbd.getCheckChiMangTenNguoi2());

                // Thông tin khác
                dto.setCheckNguoiMangTenBiaDo(tsbd.getCheckNguoiMangTenBiaDo());
                dto.setNguoiMangTen(tsbd.getNguoiMangTen());
                dto.setLandItems(tsbd.getLandItems());
                dto.setTongTaiSanBD(tsbd.getTongTaiSanBD());
                dto.setTongTaiSanBDChu(tsbd.getTongTaiSanBDChu());
                dto.setNoiDungNgoaiBia(tsbd.getNoiDungNgoaiBia());
                dto.setDienTichTS(tsbd.getDienTichTS());
                dto.setLoaiNha(tsbd.getLoaiNha());
                dto.setKetCauXayDung(tsbd.getKetCauXayDung());
                dto.setCheckTaiSanGanLienVoiDat(tsbd.getCheckTaiSanGanLienVoiDat());
                dto.setCheckDongSoHuu(tsbd.getCheckDongSoHuu());
                // Map các bảng table1, table2, table3
                List<CreditContractTableEntity> tables = tsbd.getTables();
                if (tables != null) {
                    for (CreditContractTableEntity t : tables) {
                        TableRequest tableReq = mapper.readValue(t.getTableJson(), TableRequest.class);
                        switch (t.getTableName()) {
                            case "table1": dto.setTable1(tableReq); break;
                            case "table2": dto.setTable2(tableReq); break;
                            case "table3": dto.setTable3(tableReq); break;
                        }
                    }
                }

                tsbdRequests.add(dto);
            }
            request.setTaiSanArray(tsbdRequests);
        }

        if (entity.getContractPAVV() != null) {
            CreditContractPAVVEntity pavv = entity.getContractPAVV();
            CreditContractPAVVRequest pavvDto = new CreditContractPAVVRequest();
            pavvDto.setName(pavv.getName());
            pavvDto.setAddress(pavv.getAddress());
            pavvDto.setReason(pavv.getReason());
            pavvDto.setCheckAddress(pavv.getCheckAddress());
            pavvDto.setTongVon(pavv.getTongVon());
            pavvDto.setTongVonLuuDong(pavv.getTongVonLuuDong()); // nếu có field này
            pavvDto.setVonTuCo(pavv.getVonTuCo());
            pavvDto.setVonKhac(pavv.getVonKhac());
            pavvDto.setReLoanSequence(pavv.getReLoanSequence());
            pavvDto.setVayLai(pavv.getVayLai());
            pavvDto.setHeSoVonKhac(pavv.getHeSoVonKhac());
            pavvDto.setNguoiChuyenKhoan(pavv.getNguoiChuyenKhoan());
            pavvDto.setLoaiPhuongAn(pavv.getLoaiPhuongAn());
            pavvDto.setDuNoTruoc(pavv.getDuNoTruoc());
            pavvDto.setSoTienVayLanNay(pavv.getSoTienVayLanNay());
            pavvDto.setGiaiNganHM(pavv.getGiaiNganHM());
            pavvDto.setSoGiaiNgan(pavv.getSoGiaiNgan());
            pavvDto.setNgayGiaiNgan(pavv.getNgayGiaiNgan());
            pavvDto.setNgayHDTDCu(pavv.getNgayHDTDCu());
            pavvDto.setSoHDTDCu(pavv.getSoHDTDCu());
            pavvDto.setNgayThuLaiHM(pavv.getNgayThuLaiHM());
            request.setPavvRequest(pavvDto);
        }


        // map các field cơ bản
        request.setId(entity.getId());
        request.setContractDate(entity.getContractDate() != null ? entity.getContractDate().toString() : null);
        request.setNgayTheChap(entity.getNgayTheChap() != null ? entity.getNgayTheChap().toString() : null);
        request.setNguoiDaiDien(entity.getNguoiDaiDien());
        request.setGtkh(entity.getGtkh());
        request.setTenKhachHang(entity.getTenKhachHang());
        request.setNamSinhKhachHang(entity.getNamSinhKhachHang());
        request.setPhoneKhachHang(entity.getPhoneKhachHang());
        request.setSoTheThanhVienKhachHang(entity.getSoTheThanhVienKhachHang());
        request.setCccdKhachHang(entity.getCccdKhachHang());
        request.setNgayCapCCCDKhachHang(entity.getNgayCapCCCDKhachHang());
        request.setNoiCapCCCDKhachHang(entity.getNoiCapCCCDKhachHang());
        request.setDiaChiThuongTruKhachHang(entity.getDiaChiThuongTruKhachHang());

        request.setGtnt(entity.getGtnt());
        request.setTenNguoiThan(entity.getTenNguoiThan());
        request.setNamSinhNguoiThan(entity.getNamSinhNguoiThan());
        request.setCccdNguoiThan(entity.getCccdNguoiThan());
        request.setNgayCapCCCDNguoiThan(entity.getNgayCapCCCDNguoiThan());
        request.setNoiCapCCCDNguoiThan(entity.getNoiCapCCCDNguoiThan());
        request.setDiaChiThuongTruNguoiThan(entity.getDiaChiThuongTruNguoiThan());
        request.setQuanHe(entity.getQuanHe());
        request.setTienSo(entity.getTienSo());
        request.setTienChu(entity.getTienChu());
        request.setMuchDichVay(entity.getMuchDichVay());
        request.setHanMuc(entity.getHanMuc());
        request.setLaiSuat(entity.getLaiSuat());
        request.setChoVay(entity.getChoVay());
        request.setLoaiVay(entity.getLoaiVay());
        request.setCheckOption(entity.getCheckOption());
        request.setSoHopDongTD(entity.getSoHopDongTD());
        request.setNgayKetThucKyHanVay(entity.getNgayKetThucKyHanVay());
        request.setThoiHanVay(entity.getThoiHanVay());
//        request.setTongTaiSanBD(entity.getTongTaiSanBD());
//        request.setTongTaiSanBDChu(entity.getTongTaiSanBDChu());
        request.setPhongGiaoDich(entity.getPhongGiaoDich());
        request.setDiaChiPhongGiaoDich(entity.getDiaChiPhongGiaoDich());
        request.setCheckHopDongBaoLanh(entity.getCheckHopDongBaoLanh());
        request.setSoBBXetDuyetChoVay(entity.getSoBBXetDuyetChoVay());
        if (entity.getTableJson() != null) {
            TableRequest tableReq = mapper.readValue(entity.getTableJson(), TableRequest.class);
            System.out.println("TableRequest sau khi đọc: " + tableReq.getRows());
            request.setTableRequest(tableReq);
        }
        if (entity.getAvatars() != null && !entity.getAvatars().isEmpty()) {
            List<FileMetadataDto> avatarDtos = new ArrayList<>();
            for (AvatarEntity avatar : entity.getAvatars()) {
                FileMetadataDto dto = new FileMetadataDto();
                dto.setFileName(avatar.getFileName());
                dto.setContentType(avatar.getContentType());
                // Nếu cần thêm URL để hiển thị
                dto.setFileUrl(avatar.getFileUrl());
                avatarDtos.add(dto);
            }
            request.setFileAvatarUrls(avatarDtos);
        }


        // map dữ liệu bảng phụ
        if (entity.getTables() != null) {
            for (CreditContractTableEntity t : entity.getTables()) {
                TableRequest tableReq = mapper.readValue(t.getTableJson(), TableRequest.class);
                switch (t.getTableName()) {
                    case "hanMucTable":
                        request.setHanMucTable(tableReq);
                        break;
                    case "chiPhiTable":
                        request.setChiPhiTable(tableReq);
                        break;
                    case "thuNhapDuKienTable":
                        request.setThuNhapDuKienTable(tableReq);
                        break;
                    case "phuLucHanMucTable":
                        request.setPhuLucHanMucTable(tableReq);
                        break;
                }
            }
        }

        return request;
    }


    /**
     * Xử lý avatar: di chuyển file từ thư mục tạm sang thư mục uploads,
     * tạo AvatarEntity và gắn vào CreditContractEntity
     */
    public void processAvatars(ContractRequest request,
                               CreditContractEntity entity,
                               String tempDir,
                               String uploadDir,
                               IFileMetadataRepository fileMetadataRepository) {
        if (request.getFileAvatarUrls() == null || request.getFileAvatarUrls().isEmpty()) return;

        // Danh sách file mới từ request
        List<String> newFileNames = request.getFileAvatarUrls()
                .stream()
                .map(FileMetadataDto::getFileName)
                .toList();
        System.err.println("newFileNames --> " + newFileNames);

        // Xóa avatar cũ nếu không nằm trong danh sách mới
        Iterator<AvatarEntity> iterator = entity.getAvatars().iterator();
        while (iterator.hasNext()) {
            AvatarEntity oldAvatar = iterator.next();
            if (!newFileNames.contains(oldAvatar.getFileName())) {
                try {
                    Path oldPath = Paths.get(uploadDir, oldAvatar.getFileName());
                    Files.deleteIfExists(oldPath);
                    fileMetadataRepository.deleteByFileName(oldAvatar.getFileName());
                } catch (IOException e) {
                    System.err.println("Không thể xóa file cũ: " + oldAvatar.getFileName());
                }
                iterator.remove(); // xóa khỏi entity
            }
        }

        for (FileMetadataDto dto : request.getFileAvatarUrls()) {
            try {
                String fileNameAvatar = dto.getFileName();

                boolean exists = entity.getAvatars().stream()
                        .anyMatch(a -> a.getFileName().equals(fileNameAvatar));
                if (exists) continue;

                Path tempPath = Paths.get(tempDir, fileNameAvatar);
                Path finalPath = Paths.get(uploadDir, fileNameAvatar);

                if (!Files.exists(finalPath) && Files.exists(tempPath)) {
                    Files.createDirectories(finalPath.getParent());
                    Files.move(tempPath, finalPath, StandardCopyOption.REPLACE_EXISTING);
                }

                String finalUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/uploads/")
                        .path(fileNameAvatar)
                        .toUriString();

                AvatarEntity avatar = new AvatarEntity();
                avatar.setFileName(fileNameAvatar);
                avatar.setFilePath(finalPath.toString()); // đường dẫn vật lý
                avatar.setFileUrl(finalUrl);              // URL public
                avatar.setContentType(dto.getContentType());
                avatar.setCreditContract(entity);

                entity.getAvatars().add(avatar);
            } catch (IOException e) {
                throw new RuntimeException("Không thể xử lý file avatar: " + dto.getFileName(), e);
            }
        }


    }
}
