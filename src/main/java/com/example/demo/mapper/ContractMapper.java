package com.example.demo.mapper;

import com.example.demo.dto.request.ContractRequest;
import com.example.demo.dto.request.FileMetadataDto;
import com.example.demo.model.AvatarEntity;
import com.example.demo.model.CreditContractEntity;
import com.example.demo.model.User;
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
                                   LocalDate date) {
        entity.setUser(user);
        entity.setContractDate(date);

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
        entity.setBenA(request.getBenA());
        entity.setDiaChiPhongGiaoDich(request.getDiaChiPhongGiaoDich());
        entity.setCheckNguoiMangTenBiaDo(request.getCheckNguoiMangTenBiaDo());
        entity.setNguoiMangTen(request.getNguoiMangTen());
        entity.setNoiCapCCCDKhachHang(request.getNoiCapCCCDKhachHang());
        entity.setNoiCapCCCDNguoiThan(request.getNoiCapCCCDNguoiThan());
        entity.setNoiCapCCCDDungTenBiaDo1(request.getNoiCapCCCDDungTenBiaDo1());
        entity.setNoiCapCCCDDungTenBiaDo2(request.getNoiCapCCCDDungTenBiaDo2());
        entity.setCheckHopDongBaoLanh(request.getCheckHopDongBaoLanh());
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

        // Thêm avatar mới (di chuyển từ temp sang uploads)
        for (FileMetadataDto dto : request.getFileAvatarUrls()) {
            try {
                String fileNameAvatar = dto.getFileName();

                // Kiểm tra xem avatar đã tồn tại trong entity chưa
                boolean exists = entity.getAvatars().stream()
                        .anyMatch(a -> a.getFileName().equals(fileNameAvatar));

                if (exists) {
                    System.out.println("Avatar đã tồn tại trong entity, bỏ qua: " + fileNameAvatar);
                    continue; // bỏ qua không thêm lại
                }

                Path tempPath = Paths.get(tempDir, fileNameAvatar);
                Path finalPath = Paths.get(uploadDir, fileNameAvatar);

                // Nếu file đã tồn tại ở uploadDir thì bỏ qua move
                if (Files.exists(finalPath)) {
                    System.out.println("File đã tồn tại trong uploads, bỏ qua move: " + fileNameAvatar);
                } else {
                    // Chỉ move nếu file còn trong temp
                    if (Files.exists(tempPath)) {
                        Files.createDirectories(finalPath.getParent());
                        Files.move(tempPath, finalPath, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        System.out.println("Không tìm thấy file trong temp: " + fileNameAvatar);
                    }
                }

                String finalUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/uploads/")
                        .path(fileNameAvatar)
                        .toUriString();

                AvatarEntity avatar = new AvatarEntity();
                avatar.setFilePath(finalUrl);
                avatar.setFileName(fileNameAvatar);
                avatar.setContentType(dto.getContentType());
                avatar.setCreditContract(entity);

                entity.getAvatars().add(avatar);
            } catch (IOException e) {
                throw new RuntimeException("Không thể xử lý file avatar: " + dto.getFileName(), e);
            }
        }
    }
}
