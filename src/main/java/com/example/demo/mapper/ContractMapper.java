package com.example.demo.mapper;

import com.example.demo.dto.request.ContractRequest;
import com.example.demo.dto.request.CreditContractTSBDRequest;
import com.example.demo.dto.request.FileMetadataDto;
import com.example.demo.dto.request.TableRequest;
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
                                   LocalDate dateTC,
                                   LocalDate dateBD) throws JsonProcessingException {
        entity.setUser(user);
        entity.setContractDate(date);
        entity.setNgayTheChap(dateTC);
        entity.setNgayBaoDam(dateBD);
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
        entity.setNoiDungNgoaiBia(request.getNoiDungNgoaiBia());
        entity.setTienSo(request.getTienSo());
        entity.setTienChu(request.getTienChu());
        entity.setMuchDichVay(request.getMuchDichVay());
        entity.setHanMuc(request.getHanMuc());
        entity.setLaiSuat(request.getLaiSuat());
        entity.setSoHopDongTheChapQSDD(request.getSoHopDongTheChapQSDD());

        // Thông tin bìa đỏ
        entity.setSerial(request.getSerial());
        entity.setNoiCapSo(request.getNoiCapSo());
        entity.setNgayCapSo(request.getNgayCapSo());
        entity.setNoiDungVaoSo(request.getNoiDungVaoSo());
        entity.setSoThuaDat(request.getSoThuaDat());
        entity.setSoBanDo(request.getSoBanDo());
        entity.setDiaChiThuaDat(request.getDiaChiThuaDat());
        entity.setDienTichDatSo(request.getDienTichDatSo());
        entity.setDienTichDatChu(request.getDienTichDatChu());
        entity.setHinhThucSuDung(request.getHinhThucSuDung());
        entity.setMuchDichSuDung(request.getMuchDichSuDung());
        entity.setThoiHanSuDung(request.getThoiHanSuDung());
        entity.setSoBienBanDinhGia(request.getSoBienBanDinhGia());
        entity.setNoiDungThoaThuan(request.getNoiDungThoaThuan());
        entity.setNguonGocSuDung(request.getNguonGocSuDung());
        entity.setGhiChu(request.getGhiChu());
        entity.setChoVay(request.getChoVay());
        entity.setLoaiVay(request.getLoaiVay());
        entity.setCheckOption(request.getCheckOption());
        entity.setCheckGhiChu(request.getCheckGhiChu());
        entity.setCheckNguonGocSuDung(request.getCheckNguonGocSuDung());
        entity.setSoHopDongTD(request.getSoHopDongTD());
        entity.setNgayKetThucKyHanVay(request.getNgayKetThucKyHanVay());
        entity.setDungTenBiaDo1(request.getDungTenBiaDo1());
        entity.setCheckNguoiDungTenBiaDo2(request.getCheckNguoiDungTenBiaDo2());
        entity.setDungTenBiaDo2(request.getDungTenBiaDo2());
        entity.setLandItems(request.getLandItems());
        entity.setThoiHanVay(request.getThoiHanVay());
        entity.setCheckNhaCoDinh(request.getCheckNhaCoDinh());
        entity.setNhaCoDinh(request.getNhaCoDinh());
        entity.setTongTaiSanBD(request.getTongTaiSanBD());
        entity.setTongTaiSanBDChu(request.getTongTaiSanBDChu());
        entity.setCheckMucDichSuDung(request.getCheckMucDich());
        entity.setCheckLoaiDat(request.getCheckLoaiDat());
        entity.setLoaiDat(request.getLoaiDat());
        entity.setGioiTinhDungTenBiaDo1(request.getGioiTinhDungTenBiaDo1());
        entity.setNamSinhDungTenBiaDo1(request.getNamSinhDungTenBiaDo1());
        entity.setPhoneDungTenBiaDo1(request.getPhoneDungTenBiaDo1());
        entity.setCccdDungTenBiaDo1(request.getCccdDungTenBiaDo1());
        entity.setNgayCapCCCDDungTenBiaDo1(request.getNgayCapCCCDDungTenBiaDo1());
        entity.setDiaChiThuongTruDungTenBiaDo1(request.getDiaChiThuongTruDungTenBiaDo1());
        entity.setGioiTinhDungTenBiaDo2(request.getGioiTinhDungTenBiaDo2());
        entity.setNamSinhDungTenBiaDo2(request.getNamSinhDungTenBiaDo2());
        entity.setCccdDungTenBiaDo2(request.getCccdDungTenBiaDo2());
        entity.setNgayCapCCCDDungTenBiaDo2(request.getNgayCapCCCDDungTenBiaDo2());
        entity.setDiaChiThuongTruDungTenBiaDo2(request.getDiaChiThuongTruDungTenBiaDo2());
        entity.setPhongGiaoDich(request.getPhongGiaoDich());
        entity.setDiaChiPhongGiaoDich(request.getDiaChiPhongGiaoDich());
        entity.setCheckNguoiMangTenBiaDo(request.getCheckNguoiMangTenBiaDo());
        entity.setNguoiMangTen(request.getNguoiMangTen());
        entity.setNoiCapCCCDKhachHang(request.getNoiCapCCCDKhachHang());
        entity.setNoiCapCCCDNguoiThan(request.getNoiCapCCCDNguoiThan());
        entity.setNoiCapCCCDDungTenBiaDo1(request.getNoiCapCCCDDungTenBiaDo1());
        entity.setNoiCapCCCDDungTenBiaDo2(request.getNoiCapCCCDDungTenBiaDo2());
        entity.setCheckHopDongBaoLanh(request.getCheckHopDongBaoLanh());
        entity.setSoBBXetDuyetChoVay(request.getSoBBXetDuyetChoVay());
        if (request.getTsbdRequest() != null) {
            CreditContractTSBDRequest dto = request.getTsbdRequest();
            CreditContractTSBDEntity tsbd = entity.getContractTSBD();

            if (tsbd == null) {
                tsbd = new CreditContractTSBDEntity();
                tsbd.setCreditContract(entity); // liên kết ngược
            }

            tsbd.setCheckTaiSanGanLienVoiDat(dto.getCheckTaiSanGanLienVoiDat());
            tsbd.setDienTichTS(dto.getDienTichTS());
            tsbd.setKetCauXayDung(dto.getKetCauXayDung());
            tsbd.setFromTime(dto.getFromTime());

            entity.setContractTSBD(tsbd); // liên kết xuôi
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
        if (request.getTable1() != null) {
            CreditContractTableEntity t1 = new CreditContractTableEntity();
            t1.setTableName("table1");
            t1.setTableJson(mapper.writeValueAsString(request.getTable1()));
            t1.setCreditContract(entity);
            entity.getTables().add(t1);
        }

        if (request.getTable2() != null) {
            CreditContractTableEntity t2 = new CreditContractTableEntity();
            t2.setTableName("table2");
            t2.setTableJson(mapper.writeValueAsString(request.getTable2()));
            t2.setCreditContract(entity);
            entity.getTables().add(t2);
        }

        if (request.getTable3() != null) {
            CreditContractTableEntity t3 = new CreditContractTableEntity();
            t3.setTableName("table3");
            t3.setTableJson(mapper.writeValueAsString(request.getTable3()));
            t3.setCreditContract(entity);
            entity.getTables().add(t3);
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
        if (entity.getContractTSBD() != null) {
            CreditContractTSBDEntity tsbd = entity.getContractTSBD();

            CreditContractTSBDRequest dto = new CreditContractTSBDRequest();
            dto.setCheckTaiSanGanLienVoiDat(tsbd.getCheckTaiSanGanLienVoiDat());
            dto.setDienTichTS(tsbd.getDienTichTS());
            dto.setKetCauXayDung(tsbd.getKetCauXayDung());
            dto.setFromTime(tsbd.getFromTime());

            request.setTsbdRequest(dto);
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
        request.setNoiDungNgoaiBia(entity.getNoiDungNgoaiBia());
        request.setTienSo(entity.getTienSo());
        request.setTienChu(entity.getTienChu());
        request.setMuchDichVay(entity.getMuchDichVay());
        request.setHanMuc(entity.getHanMuc());
        request.setLaiSuat(entity.getLaiSuat());
        request.setSoHopDongTheChapQSDD(entity.getSoHopDongTheChapQSDD());

        // Thông tin bìa đỏ
        request.setSerial(entity.getSerial());
        request.setNoiCapSo(entity.getNoiCapSo());
        request.setNgayCapSo(entity.getNgayCapSo());
        request.setNoiDungVaoSo(entity.getNoiDungVaoSo());
        request.setSoThuaDat(entity.getSoThuaDat());
        request.setSoBanDo(entity.getSoBanDo());
        request.setDiaChiThuaDat(entity.getDiaChiThuaDat());
        request.setDienTichDatSo(entity.getDienTichDatSo());
        request.setDienTichDatChu(entity.getDienTichDatChu());
        request.setHinhThucSuDung(entity.getHinhThucSuDung());
        request.setMuchDichSuDung(entity.getMuchDichSuDung());
        request.setThoiHanSuDung(entity.getThoiHanSuDung());
        request.setSoBienBanDinhGia(entity.getSoBienBanDinhGia());
        request.setNoiDungThoaThuan(entity.getNoiDungThoaThuan());
        request.setNguonGocSuDung(entity.getNguonGocSuDung());
        request.setGhiChu(entity.getGhiChu());
        request.setChoVay(entity.getChoVay());
        request.setLoaiVay(entity.getLoaiVay());
        request.setCheckOption(entity.getCheckOption());
        request.setCheckGhiChu(entity.getCheckGhiChu());
        request.setCheckNguonGocSuDung(entity.getCheckNguonGocSuDung());
        request.setSoHopDongTD(entity.getSoHopDongTD());
        request.setNgayKetThucKyHanVay(entity.getNgayKetThucKyHanVay());
        request.setDungTenBiaDo1(entity.getDungTenBiaDo1());
        request.setCheckNguoiDungTenBiaDo2(entity.getCheckNguoiDungTenBiaDo2());
        request.setDungTenBiaDo2(entity.getDungTenBiaDo2());
        request.setLandItems(entity.getLandItems());
        request.setThoiHanVay(entity.getThoiHanVay());
        request.setCheckNhaCoDinh(entity.getCheckNhaCoDinh());
        request.setNhaCoDinh(entity.getNhaCoDinh());
        request.setTongTaiSanBD(entity.getTongTaiSanBD());
        request.setTongTaiSanBDChu(entity.getTongTaiSanBDChu());
        request.setCheckMucDich(entity.getCheckMucDichSuDung());
        request.setCheckLoaiDat(entity.getCheckLoaiDat());
        request.setLoaiDat(entity.getLoaiDat());
        request.setGioiTinhDungTenBiaDo1(entity.getGioiTinhDungTenBiaDo1());
        request.setNamSinhDungTenBiaDo1(entity.getNamSinhDungTenBiaDo1());
        request.setPhoneDungTenBiaDo1(entity.getPhoneDungTenBiaDo1());
        request.setCccdDungTenBiaDo1(entity.getCccdDungTenBiaDo1());
        request.setNgayCapCCCDDungTenBiaDo1(entity.getNgayCapCCCDDungTenBiaDo1());
        request.setNoiCapCCCDDungTenBiaDo1(entity.getNoiCapCCCDDungTenBiaDo1());
        request.setDiaChiThuongTruDungTenBiaDo1(entity.getDiaChiThuongTruDungTenBiaDo1());
        request.setGioiTinhDungTenBiaDo2(entity.getGioiTinhDungTenBiaDo2());
        request.setNamSinhDungTenBiaDo2(entity.getNamSinhDungTenBiaDo2());
        request.setCccdDungTenBiaDo2(entity.getCccdDungTenBiaDo2());
        request.setNgayCapCCCDDungTenBiaDo2(entity.getNgayCapCCCDDungTenBiaDo2());
        request.setNoiCapCCCDDungTenBiaDo2(entity.getNoiCapCCCDDungTenBiaDo2());
        request.setDiaChiThuongTruDungTenBiaDo2(entity.getDiaChiThuongTruDungTenBiaDo2());
        request.setPhongGiaoDich(entity.getPhongGiaoDich());
        request.setDiaChiPhongGiaoDich(entity.getDiaChiPhongGiaoDich());
        request.setCheckNguoiMangTenBiaDo(entity.getCheckNguoiMangTenBiaDo());
        request.setNguoiMangTen(entity.getNguoiMangTen());
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
                    case "table1":
                        request.setTable1(tableReq);
                        break;
                    case "table2":
                        request.setTable2(tableReq);
                        break;
                    case "table3":
                        request.setTable3(tableReq);
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
        System.err.println("newFileNames --> "+newFileNames);

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
