package com.example.demo.service.creditcontract;

import com.example.demo.config.WordGenerator;
import com.example.demo.dto.request.ContractRequest;
import com.example.demo.model.AvatarEntity;
import com.example.demo.model.CreditContractEntity;
import com.example.demo.model.User;
import com.example.demo.repository.ICreditContractRepository;
import com.example.demo.security.userprincal.UserDetailService;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CreditContractServiceIMPL implements ICreditContractService{
    @Autowired
    private ICreditContractRepository creditContractRepository;
    @Autowired
    private UserDetailService userDetailService;
    private  WordGenerator wordGenerator;
    @Override
    public List<CreditContractEntity> findAll() {
        return List.of();
    }

    @Override
    public void save(CreditContractEntity creditContractEntity) {
    }

    @Override
    public Page<CreditContractEntity> findAll(Pageable pageable) {
        return null;
    }
    public byte[] generatePreview(ContractRequest dto, String fileType) {
        return wordGenerator.generateWord(dto, fileType);
    }
    @Override
    public Optional<CreditContractEntity> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Value("${contract.files.dir}")
    private String contractFilesDir;
    @Override
    public List<String> generateContractFilesPreview(ContractRequest request) throws IOException {
        User user = userDetailService.getCurrentUser();
        LocalDate date = LocalDate.parse(request.getContractDate());

        List<String> fileUrls = new ArrayList<>();

        // File 1
        fileUrls.add(generateContractFile(request, date, user, "File1.docx"));

        // File 2
        fileUrls.add(generateContractFile(request, date, user, "File2.docx"));

        // File 3
        fileUrls.add(generateContractFile(request, date, user, "File3.docx"));

        return fileUrls;
    }

    @NotNull
    private String generateContractFile(@NotNull ContractRequest request,
                                        @NotNull LocalDate date,
                                        @NotNull User user,
                                        @NotNull String templateName) throws IOException {

        try (InputStream is = new ClassPathResource("templates/" + templateName).getInputStream();
             XWPFDocument doc = new XWPFDocument(is)) {

            // 👉 Thay thế placeholder
            replacePlaceholders(doc, request, date);

            // 👉 Tạo tên file duy nhất
            String fileName = templateName.replace(".docx", "")
                    + "_" + user.getId()
                    + "_" + System.currentTimeMillis()
                    + ".docx";

            Path outputPath = Paths.get(contractFilesDir, fileName);
            Files.createDirectories(outputPath.getParent());

            try (OutputStream os = Files.newOutputStream(outputPath)) {
                doc.write(os);
            }

            // 👉 Trả về URL công khai cho frontend
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/")
                    .path(fileName)
                    .toUriString();
        }
    }
    public List<String> generateContractFilesExport(ContractRequest request) throws IOException {
        User user = userDetailService.getCurrentUser();
        LocalDate date = LocalDate.parse(request.getContractDate());

        List<String> fileUrls = new ArrayList<>();

        // File 1
        fileUrls.add(generateContractFileExport(request, date, user, "File1.docx"));

        // File 2
        fileUrls.add(generateContractFileExport(request, date, user, "File2.docx"));

        // File 3
        fileUrls.add(generateContractFileExport(request, date, user, "File3.docx"));
        // Map dữ liệu từ request sang entity
        CreditContractEntity entity = new CreditContractEntity();
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

        if (request.getFileAvatarUrls() != null) {
            for (String url : request.getFileAvatarUrls()) {
                AvatarEntity avatar = new AvatarEntity();

                // Lưu trực tiếp URL để frontend hiển thị
                avatar.setFilePath(url);

                // Tách tên file từ URL (sau dấu '/')
                String fileNameAvatar = url.substring(url.lastIndexOf('/') + 1);
                avatar.setFileName(fileNameAvatar);

                // Nếu bạn biết loại file (jpg/png), có thể set cứng hoặc parse từ fileName
                if (fileNameAvatar.toLowerCase().endsWith(".png")) {
                    avatar.setContentType("image/png");
                } else if (fileNameAvatar.toLowerCase().endsWith(".jpg") || fileNameAvatar.toLowerCase().endsWith(".jpeg")) {
                    avatar.setContentType("image/jpeg");
                } else {
                    avatar.setContentType("application/octet-stream"); // fallback
                }

                avatar.setCreditContract(entity);
                entity.getAvatars().add(avatar);
            }
        }
        creditContractRepository.save(entity);
        return fileUrls;
    }

    @NotNull
    public String generateContractFileExport(@NotNull ContractRequest request,
                                             @NotNull LocalDate date,
                                             @NotNull User user,
                                             @NotNull String templateName) throws IOException {

        try (InputStream is = new ClassPathResource("templates/" + templateName).getInputStream();
             XWPFDocument doc = new XWPFDocument(is)) {

            // Thay thế placeholder
            replacePlaceholders(doc, request, date);

            // Tạo tên file duy nhất
            String fileName = templateName.replace(".docx", "")
                    + "_export_" + user.getId()
                    + "_" + System.currentTimeMillis()
                    + ".docx";

            Path outputPath = Paths.get(contractFilesDir, fileName);
            Files.createDirectories(outputPath.getParent());

            try (OutputStream os = Files.newOutputStream(outputPath)) {
                doc.write(os);
            }



            // Trả về URL công khai cho frontend
//            return ServletUriComponentsBuilder.fromCurrentContextPath()
//                    .path("/files/")
//                    .path(fileName)
//                    .toUriString();
            return outputPath.toString();
        }
    }

    private void replacePlaceholders(XWPFDocument doc, ContractRequest request, LocalDate date) {
        Map<String, String> replacements = Map.ofEntries(
                Map.entry("{{gd}}", Optional.ofNullable(request.getNguoiDaiDien()).orElse("")),
                Map.entry("{{gtkh}}", Optional.ofNullable(request.getGtkh()).orElse("")),
                Map.entry("{{kh}}", Optional.ofNullable(request.getTenKhachHang()).orElse("")),
                Map.entry("{{nskh}}", Optional.ofNullable(request.getNamSinhKhachHang()).orElse("")),
                Map.entry("{{sdtkh}}", Optional.ofNullable(request.getPhoneKhachHang()).orElse("")),
                Map.entry("{{sttv}}", Optional.ofNullable(request.getSoTheThanhVienKhachHang()).orElse("")),
                Map.entry("{{cccdkh}}", Optional.ofNullable(request.getCccdKhachHang()).orElse("")),
                Map.entry("{{nckh}}", Optional.ofNullable(request.getNgayCapCCCDKhachHang()).orElse("")),
                Map.entry("{{ttkh}}", Optional.ofNullable(request.getDiaChiThuongTruKhachHang()).orElse("")),
                Map.entry("{{gtnt}}", Optional.ofNullable(request.getGtnt()).orElse("")),
                Map.entry("{{ntkh}}", Optional.ofNullable(request.getTenNguoiThan()).orElse("")),
                Map.entry("{{nsnt}}", Optional.ofNullable(request.getNamSinhNguoiThan()).orElse("")),
                Map.entry("{{cccdnt}}", Optional.ofNullable(request.getCccdNguoiThan()).orElse("")),
                Map.entry("{{ncnt}}", Optional.ofNullable(request.getNgayCapCCCDNguoiThan()).orElse("")),
                Map.entry("{{ttnt}}", Optional.ofNullable(request.getDiaChiThuongTruNguoiThan()).orElse("")),
                Map.entry("{{qh}}", Optional.ofNullable(request.getQuanHe()).orElse("")),
                Map.entry("{{tienso}}", Optional.ofNullable(request.getTienSo()).orElse("")),
                Map.entry("{{tc}}", Optional.ofNullable(request.getTienChu()).orElse("")),
                Map.entry("{{mdvay}}", Optional.ofNullable(request.getMuchDichVay()).orElse("")),
                Map.entry("{{hm}}", Optional.ofNullable(request.getHanMuc()).orElse("")),
                Map.entry("{{ls}}", Optional.ofNullable(request.getLaiSuat()).orElse("")),
                Map.entry("{{shdtc}}", Optional.ofNullable(request.getSoHopDongTheChapQSDD()).orElse("")),
                Map.entry("{{seri}}", Optional.ofNullable(request.getSerial()).orElse("")),
                Map.entry("{{nc}}", Optional.ofNullable(request.getNoiCapSo()).orElse("")),
                Map.entry("{{ngc}}", Optional.ofNullable(request.getNgayCapSo()).orElse("")),
                Map.entry("{{vsmt}}", Optional.ofNullable(request.getNoiDungVaoSo()).orElse("")),
                Map.entry("{{std}}", Optional.ofNullable(request.getSoThuaDat()).orElse("")),
                Map.entry("{{sbd}}", Optional.ofNullable(request.getSoBanDo()).orElse("")),
                Map.entry("{{dctd}}", Optional.ofNullable(request.getDiaChiThuaDat()).orElse("")),
                Map.entry("{{dt}}", Optional.ofNullable(request.getDienTichDatSo()).orElse("")),
                Map.entry("{{dtc}}", Optional.ofNullable(request.getDienTichDatChu()).orElse("")),
                Map.entry("{{htsd}}", Optional.ofNullable(request.getHinhThucSuDung()).orElse("")),
                Map.entry("{{mdsd}}", Optional.ofNullable(request.getMuchDichSuDung()).orElse("")),
                Map.entry("{{thsd}}", Optional.ofNullable(request.getThoiHanSuDung()).orElse("")),
                Map.entry("{{bbdg}}", Optional.ofNullable(request.getSoBienBanDinhGia()).orElse("")),
                Map.entry("{{ndtt}}", Optional.ofNullable(request.getNoiDungThoaThuan()).orElse("")),
                Map.entry("{{ngsd}}", Optional.ofNullable(request.getNguonGocSuDung()).orElse("")),
                Map.entry("{{gc}}", Optional.ofNullable(request.getGhiChu()).orElse("")),
                Map.entry("{{day}}", String.format("%02d", date.getDayOfMonth())),
                Map.entry("{{month}}", String.format("%02d", date.getMonthValue())),
                Map.entry("{{year}}", String.valueOf(date.getYear()))
        );

        // Duyệt trên bản copy để tránh ConcurrentModificationException
        List<XWPFParagraph> docParas = new ArrayList<>(doc.getParagraphs());
        for (XWPFParagraph paragraph : docParas) {
            processParagraph(paragraph, replacements);
        }

        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                List<XWPFTableCell> cells = new ArrayList<>(row.getTableCells());
                for (XWPFTableCell cell : cells) {
                    List<XWPFParagraph> paras = new ArrayList<>(cell.getParagraphs());
                    for (XWPFParagraph paragraph : paras) {
                        processParagraph(paragraph, replacements);
                    }
                }
            }
        }
    }

    private void processParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {
        List<XWPFRun> runs = new ArrayList<>(paragraph.getRuns());
        if (runs.isEmpty()) return;

        // Ghép toàn bộ text của paragraph
        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null) fullText.append(text);
        }
        String paragraphText = fullText.toString();
        if (paragraphText.isEmpty()) return;

        // Thay thế placeholder trên toàn chuỗi
        String replaced = paragraphText;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            replaced = replaced.replace(entry.getKey(), entry.getValue());
        }

        // Nếu sau thay thế rỗng → xóa paragraph
        if (replaced.trim().isEmpty()) {
            IBody body = paragraph.getBody();
            if (body instanceof XWPFDocument doc) {
                int pos = doc.getPosOfParagraph(paragraph);
                if (pos >= 0) doc.removeBodyElement(pos);
            } else if (body instanceof XWPFTableCell cell) {
                int idx = cell.getParagraphs().indexOf(paragraph);
                if (idx >= 0) cell.removeParagraph(idx);
            }
            return;
        }

        // Gán lại text cho run đầu tiên, giữ nguyên định dạng
        boolean firstTextRun = true;
        for (XWPFRun run : runs) {
            if (run.getCTR() != null && run.getCTR().getDrawingList().size() > 0) {
                // Run chứa hình vẽ → giữ nguyên
                continue;
            }
            if (firstTextRun) {
                run.setText(replaced, 0); // gán toàn bộ text đã thay thế vào run đầu tiên
                firstTextRun = false;
            } else {
                run.setText("", 0); // clear các run text còn lại
            }
        }
    }


}
