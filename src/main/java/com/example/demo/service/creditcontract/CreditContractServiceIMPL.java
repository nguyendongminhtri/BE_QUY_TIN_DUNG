package com.example.demo.service.creditcontract;

import com.example.demo.dto.request.ContractRequest;
import com.example.demo.dto.request.TableRequest;
import com.example.demo.mapper.ContractMapper;
import com.example.demo.model.CreditContractEntity;
import com.example.demo.model.FileMetadataEntity;
import com.example.demo.model.User;
import com.example.demo.repository.ICreditContractRepository;
import com.example.demo.repository.IFileMetadataRepository;
import com.example.demo.security.userprincal.UserDetailService;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;


@Service
public class CreditContractServiceIMPL implements ICreditContractService {
    @Autowired
    private ICreditContractRepository creditContractRepository;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private IFileMetadataRepository fileMetadataRepository;

    @Autowired
    private ContractMapper contractMapper;

    @Override
    public List<CreditContractEntity> findAll() {
        return creditContractRepository.findAll();
    }

    @Override
    public void save(CreditContractEntity creditContractEntity) {
    }

    @Override
    public Page<CreditContractEntity> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<CreditContractEntity> findById(Long id) {
        return creditContractRepository.findById(id);
    }

    @Value("${contract.files.dir}")
    private String contractFilesDir;

    @Value("${contract.temp.dir}")
    private String tempDir;

    @Value("${contract.uploads.dir}")
    private String uploadDir;

    // 👉 Preview: chỉ sinh file, không lưu DB
    @Override
    public List<String> generateContractFilesPreview(ContractRequest request) throws IOException {
        User user = userDetailService.getCurrentUser();
        LocalDate date = LocalDate.parse(request.getContractDate());

        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(generateContractFile(request, date, user, "File1.docx"));
        fileUrls.add(generateContractFile(request, date, user, "File2.docx"));
        fileUrls.add(generateContractFile(request, date, user, "File3.docx"));
        fileUrls.add(generateContractFile(request, date, user, "File4.docx"));

        return fileUrls;
    }

    // 👉 Export: tạo mới hợp đồng và lưu DB
    @Transactional
    public List<String> generateContractFilesExport(ContractRequest request) throws IOException {
        User user = userDetailService.getCurrentUser();
        LocalDate date = LocalDate.parse(request.getContractDate());

        CreditContractEntity entity = new CreditContractEntity();
        contractMapper.mapRequestToEntity(request, entity, user, date);
        contractMapper.processAvatars(request, entity, tempDir, uploadDir, fileMetadataRepository);

        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(generateContractFileExport(request, date, user, "File1.docx"));
        fileUrls.add(generateContractFileExport(request, date, user, "File2.docx"));
        fileUrls.add(generateContractFileExport(request, date, user, "File3.docx"));
        fileUrls.add(generateContractFileExport(request, date, user, "File4.docx"));

        creditContractRepository.save(entity);
        return fileUrls;
    }

    // 👉 Export: update hợp đồng đã có
    @Transactional
    public List<String> updateContractFilesExport(Long id, ContractRequest request) throws IOException {
        User user = userDetailService.getCurrentUser();
        LocalDate date = LocalDate.parse(request.getContractDate());

        CreditContractEntity entity = creditContractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        contractMapper.mapRequestToEntity(request, entity, user, date);
        contractMapper.processAvatars(request, entity, tempDir, uploadDir, fileMetadataRepository);

        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(generateContractFileExport(request, date, user, "File1.docx"));
        fileUrls.add(generateContractFileExport(request, date, user, "File2.docx"));
        fileUrls.add(generateContractFileExport(request, date, user, "File3.docx"));
        fileUrls.add(generateContractFileExport(request, date, user, "File4.docx"));

        creditContractRepository.save(entity);
        return fileUrls;
    }

    // 👉 Hàm generate file (preview)
    private String generateContractFile(ContractRequest request, LocalDate date, User user, String templateName) throws IOException {
        try (InputStream is = new ClassPathResource("templates/" + templateName).getInputStream();
             XWPFDocument doc = new XWPFDocument(is)) {

            replacePlaceholders(doc, request, date);

            String fileName = templateName.replace(".docx", "")
                    + "_" + user.getId()
                    + "_" + System.currentTimeMillis()
                    + ".docx";

            Path outputPath = Paths.get(contractFilesDir, fileName);
            Files.createDirectories(outputPath.getParent());

            try (OutputStream os = Files.newOutputStream(outputPath)) {
                doc.write(os);
            }

            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/")
                    .path(fileName)
                    .toUriString();
        }
    }

    @NotNull
    public String generateContractFileExport(@NotNull ContractRequest request,
                                             @NotNull LocalDate date,
                                             @NotNull User user,
                                             @NotNull String templateName) throws IOException {
        try (InputStream is = new ClassPathResource("templates/" + templateName).getInputStream();
             XWPFDocument doc = new XWPFDocument(is)) {

            replacePlaceholders(doc, request, date);

            String fileName = templateName.replace(".docx", "")
                    + "_export_" + user.getId()
                    + "_" + System.currentTimeMillis()
                    + ".docx";

            Path outputPath = Paths.get(contractFilesDir, fileName);
            Files.createDirectories(outputPath.getParent());

            try (OutputStream os = Files.newOutputStream(outputPath)) {
                doc.write(os);
            }

            // ✅ Trả về đường dẫn vật lý
            return outputPath.toString();
        }
    }


    private void replacePlaceholders(XWPFDocument doc, ContractRequest request, LocalDate date) {
        System.err.println("request --> " + request);
        Map<String, String> replacements = new HashMap<>(); // Các placeholder mặc định
//        replacements.put("{{gd}}", Optional.ofNullable(request.getNguoiDaiDien()).orElse(""));
        replacements.put("{{shdtd}}", Optional.ofNullable(request.getSoHopDongTD()).orElse(""));
        replacements.put("{{gtkh}}", Optional.ofNullable(request.getGtkh()).orElse(""));
        replacements.put("{{gtkht}}", Optional.ofNullable(request.getGtkh().toLowerCase()).orElse(""));
        replacements.put("{{kh}}", Optional.ofNullable(request.getTenKhachHang()).orElse(""));
        replacements.put("{{nskh}}", Optional.ofNullable(request.getNamSinhKhachHang()).orElse(""));
        replacements.put("{{sdtkh}}", Optional.ofNullable(request.getPhoneKhachHang()).orElse(""));
        replacements.put("{{sttv}}", Optional.ofNullable(request.getSoTheThanhVienKhachHang()).orElse(""));
        replacements.put("{{cccdkh}}", Optional.ofNullable(request.getCccdKhachHang()).orElse(""));
        replacements.put("{{nckh}}", Optional.ofNullable(request.getNgayCapCCCDKhachHang()).orElse(""));
        replacements.put("{{nccccdkh}}", Optional.ofNullable(request.getNoiCapCCCDKhachHang()).orElse(""));
        replacements.put("{{ttkh}}", Optional.ofNullable(request.getDiaChiThuongTruKhachHang()).orElse(""));
        replacements.put("{{gtnt}}", Optional.ofNullable(request.getGtnt()).orElse(""));
        replacements.put("{{ntkh}}", Optional.ofNullable(request.getTenNguoiThan()).orElse(""));
        replacements.put("{{nsnt}}", Optional.ofNullable(request.getNamSinhNguoiThan()).orElse(""));
        replacements.put("{{cccdnt}}", Optional.ofNullable(request.getCccdNguoiThan()).orElse(""));
        replacements.put("{{ncnt}}", Optional.ofNullable(request.getNgayCapCCCDNguoiThan()).orElse(""));
        replacements.put("{{nccccdnt}}", Optional.ofNullable(request.getNoiCapCCCDNguoiThan()).orElse(""));
        replacements.put("{{ttnt}}", Optional.ofNullable(request.getDiaChiThuongTruNguoiThan()).orElse(""));
        replacements.put("{{qh}}", Optional.ofNullable(request.getQuanHe()).orElse(""));
        replacements.put("{{tienso}}", Optional.ofNullable(request.getTienSo()).orElse(""));
        replacements.put("{{tc}}", Optional.ofNullable(request.getTienChu()).orElse(""));
        replacements.put("{{mdvay}}", Optional.ofNullable(request.getMuchDichVay()).orElse(""));
        replacements.put("{{hm}}", Optional.ofNullable(request.getHanMuc()).orElse(""));
        replacements.put("{{ls}}", Optional.ofNullable(request.getLaiSuat()).orElse(""));
        replacements.put("{{nkt}}", Optional.ofNullable(request.getNgayKetThucKyHanVay()).orElse(""));
        replacements.put("{{shdtc}}", Optional.ofNullable(request.getSoHopDongTheChapQSDD()).orElse(""));
        replacements.put("{{seri}}", Optional.ofNullable(request.getSerial()).orElse(""));
        replacements.put("{{nc}}", Optional.ofNullable(request.getNoiCapSo()).orElse(""));
        replacements.put("{{ngc}}", Optional.ofNullable(request.getNgayCapSo()).orElse(""));
        replacements.put("{{vsmt}}", Optional.ofNullable(request.getNoiDungVaoSo()).orElse(""));
        replacements.put("{{std}}", Optional.ofNullable(request.getSoThuaDat()).orElse(""));
        replacements.put("{{sbd}}", Optional.ofNullable(request.getSoBanDo()).orElse(""));
        replacements.put("{{dctd}}", Optional.ofNullable(request.getDiaChiThuaDat()).orElse(""));
        replacements.put("{{dt}}", Optional.ofNullable(request.getDienTichDatSo()).orElse(""));
        replacements.put("{{dtc}}", Optional.ofNullable(request.getDienTichDatChu()).orElse(""));
        replacements.put("{{htsd}}", Optional.ofNullable(request.getHinhThucSuDung()).orElse(""));
        replacements.put("{{mdsd}}", Optional.ofNullable(request.getMuchDichSuDung()).orElse(""));
        replacements.put("{{thsd}}", Optional.ofNullable(request.getThoiHanSuDung()).orElse(""));
        replacements.put("{{bbdg}}", Optional.ofNullable(request.getSoBienBanDinhGia()).orElse(""));
        replacements.put("{{ndtt}}", Optional.ofNullable(request.getNoiDungThoaThuan()).orElse(""));
        replacements.put("{{ngsd}}", Optional.ofNullable(request.getNguonGocSuDung()).orElse(""));
        replacements.put("{{gc}}", Optional.ofNullable(request.getGhiChu()).orElse(""));
        replacements.put("{{chv}}", Optional.ofNullable(request.getChoVay()).orElse(""));
        replacements.put("{{khbd}}", Optional.ofNullable(request.getDungTenBiaDo1()).orElse(""));
        replacements.put("{{gtkhbd}}", Optional.ofNullable(request.getGioiTinhDungTenBiaDo1()).orElse(""));
        replacements.put("{{gtkhbdt}}", Optional.ofNullable(request.getGioiTinhDungTenBiaDo1().toLowerCase()).orElse(""));
        replacements.put("{{nskhbd}}", Optional.ofNullable(request.getNamSinhDungTenBiaDo1()).orElse(""));
        replacements.put("{{cccdkhbd}}", Optional.ofNullable(request.getCccdDungTenBiaDo1()).orElse(""));
        replacements.put("{{sdtkhbd}}", Optional.ofNullable(request.getPhoneDungTenBiaDo1()).orElse(""));
        replacements.put("{{ngckhbd}}", Optional.ofNullable(request.getNgayCapCCCDDungTenBiaDo1()).orElse(""));
        replacements.put("{{nccccdkhbd}}", Optional.ofNullable(request.getNoiCapCCCDDungTenBiaDo1()).orElse(""));
        replacements.put("{{dckhbd}}", Optional.ofNullable(request.getDiaChiThuongTruDungTenBiaDo1()).orElse(""));
        replacements.put("{{ntbd}}", Optional.ofNullable(request.getDungTenBiaDo2()).orElse(""));
        replacements.put("{{gtntbd}}", Optional.ofNullable(request.getGioiTinhDungTenBiaDo2()).orElse(""));
        replacements.put("{{cccdntbd}}", Optional.ofNullable(request.getCccdDungTenBiaDo2()).orElse(""));
        replacements.put("{{ngcntbd}}", Optional.ofNullable(request.getNgayCapCCCDDungTenBiaDo2()).orElse(""));
        replacements.put("{{nccccdntbd}}", Optional.ofNullable(request.getNoiCapCCCDDungTenBiaDo2()).orElse(""));
        replacements.put("{{dcntbd}}", Optional.ofNullable(request.getDiaChiThuongTruDungTenBiaDo2()).orElse(""));
        replacements.put("{{nsntbd}}", Optional.ofNullable(request.getNamSinhDungTenBiaDo2()).orElse(""));
        replacements.put("{{lv}}", Optional.ofNullable(request.getLoaiVay()).orElse(""));
        replacements.put("{{land_items}}", Optional.ofNullable(request.getLandItems()).orElse(""));
        replacements.put("{{thv}}", Optional.ofNullable(request.getThoiHanVay()).orElse(""));
        replacements.put("{{ncd}}", Optional.ofNullable(request.getNhaCoDinh()).orElse(""));
        replacements.put("{{tsbds}}", Optional.ofNullable(request.getTongTaiSanBD()).orElse(""));
        replacements.put("{{tsbdc}}", Optional.ofNullable(request.getTongTaiSanBDChu()).orElse(""));
        replacements.put("{{phuong}}", extractPhuong(request.getDiaChiThuongTruKhachHang()));
        replacements.put("{{day}}", String.format("%02d", date.getDayOfMonth()));
        replacements.put("{{month}}", String.format("%02d", date.getMonthValue()));
        replacements.put("{{year}}", String.valueOf(date.getYear()));
        // Thêm placeholder mới dựa vào biến checkNguoiDungTenBiaDo2
        if (request.getCheckNguoiDungTenBiaDo2()) {
            replacements.put("{{sng}}", ";Sinh ngày:");
            replacements.put("{{scccd}}", "CCCD số:");
            replacements.put("{{cng}}", "; Cấp ngày:");
            replacements.put("{{nccccd}}", "; Nơi cấp: Cục cảnh sát QLHC về TTXH.");
            replacements.put("{{dctt}}", "Địa chỉ thường trú:");
//            replacements.put("{{gtntbd}}", request.getGioiTinhDungTenBiaDo2());
        } else {
            replacements.put("{{sng}}", "");
            replacements.put("{{scccd}}", "");
            replacements.put("{{cng}}", "");
            replacements.put("{{nccccd}}", "");
            replacements.put("{{dctt}}", "");
        }
        if (request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN")) {
            replacements.put("{{loaivay4}}", "Cho vay ngắn hạn");
        } else if (request.getLoaiVay().equalsIgnoreCase("TRUNG HẠN")) {
            replacements.put("{{loaivay4}}", "Cho vay trung hạn");
        }
        if (request.getCheckNguoiMangTenBiaDo()) {
            replacements.put("{{ndtbd}}", request.getNguoiMangTen());
        } else {
            String nguoiMangTen = request.getGioiTinhDungTenBiaDo1().toLowerCase() + " " + capitalizeWords(request.getDungTenBiaDo1());
            if (request.getCheckNguoiDungTenBiaDo2()) {
                nguoiMangTen += " ";
                nguoiMangTen += request.getGioiTinhDungTenBiaDo2().toLowerCase();
                nguoiMangTen += " ";
                nguoiMangTen += capitalizeWords(request.getDungTenBiaDo2());
            }
            System.err.println("nguoiMangTen: " + nguoiMangTen);
            replacements.put("{{ndtbd}}", nguoiMangTen);
        }
        if (request.getNguoiDaiDien().equalsIgnoreCase("gd")) {
            replacements.put("{{dcpgd}}", "Trụ sở tại: Số 178 Ninh Chấp 5; phường Chu Văn An, thành phố Hải Phòng.\n" +
                    "Giấy phép đăng ký kinh doanh: 0800001806; Điện thoại: 02203.882.700\n");
            replacements.put("{{ndd}}", "bà: PHÙNG THỊ LOAN Chức vụ: Giám Đốc điều hành\n" +
                    "CCCD số: 030182016564; Cấp ngày: 22/12/2021\n");
            replacements.put("{{pgd}}", "");
        } else if (request.getNguoiDaiDien().equalsIgnoreCase("pgd")) {
            replacements.put("{{pgd}}", "-PHÒNG GIAO DỊCH AN LẠC");
            replacements.put("{{dcpgd}}", "Địa chỉ: Bờ Đa, phường Lê Đại Hành, thành phố Hải Phòng.");
            replacements.put("{{ndd}}", "ông: VŨ THANH HẢI Chức vụ: Phó Giám Đốc - Trưởng PBD An Lạc.\n" +
                    "CCCD số: 030083003225;\n" +
                    "(Theo văn bản ủy quyền số: 01/2023/UQ-TN Ngày 10 tháng 02 năm 2023)");
        }
        if (request.getCheckHopDongBaoLanh()) {
            String doanVanBan = "Bên B dùng tài sản này để đảm bảo việc thanh toán được kịp thời, đầy đủ và thực hiện một cách " +
                    "trọn vẹn khi đến hạn các nghĩa vụ trả nợ đối với hợp đồng cho vay số:" + request.getSoHopDongTD() +"của "
                    + request.getGtkh().toLowerCase() + " " + capitalizeWords(request.getTenKhachHang())+ " "+ request.getGtnt().toLowerCase() + " " +
                    capitalizeWords(request.getTenNguoiThan()) + " hoặc các hợp đồng cho vay khác có tham chiếu từ hợp đồng thế chấp này";
            replacements.put("{{tstc}}", doanVanBan);
        } else {
            String doanVanBan = "Để đảm bảo việc thanh toán được kịp thời, đầy đủ và thực hiện một cách trọn vẹn khi đến hạn các nghĩa vụ trả nợ đang " +
                    "tồn tại hoặc sẽ phát sinh trong tương lai của Bên B cho Bên A theo các " +
                    "Hợp đồng cho vay và/hoặc các Hợp đồng khác có tham chiếu từ Hợp đồng này";
            replacements.put("{{tstc}}", doanVanBan);
        }
// Tìm paragraph có placeholder
        for (XWPFParagraph para : new ArrayList<>(doc.getParagraphs())) {
            String text = para.getText();
            if (text != null && text.contains("{{TABLE_PLACEHOLDER}}")) {
                // Xóa nội dung placeholder
                for (int i = para.getRuns().size() - 1; i >= 0; i--) {
                    para.removeRun(i);
                }

                // Nếu người dùng có chọn "Có bảng dữ liệu" thì mới tạo bảng
                if (request.getTableRequest() != null && request.getTableRequest().isDrawTable()) {
                    XmlCursor cursor = para.getCTP().newCursor();
                    XWPFTable table = doc.insertNewTbl(cursor);

                    if (table != null) {
                        fillInsertedTable(table, request.getTableRequest());
                    } else {
                        System.err.println("Không tạo được bảng tại {{TABLE_PLACEHOLDER}}");
                    }
                }

                // Luôn xóa paragraph placeholder để không còn dư
                IBody body = para.getBody();
                if (body instanceof XWPFDocument d) {
                    int pos = d.getPosOfParagraph(para);
                    if (pos >= 0) d.removeBodyElement(pos);
                } else if (body instanceof XWPFTableCell cell) {
                    int idx = cell.getParagraphs().indexOf(para);
                    if (idx >= 0) cell.removeParagraph(idx);
                }
            }
        }
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

    private void copyStyle(XWPFRun source, XWPFRun target) {
        if (source.getCTR() != null && source.getCTR().getRPr() != null) {
            target.getCTR().setRPr(source.getCTR().getRPr());
        }
    }

    private String capitalizeWords(String str) {
        str = str.toLowerCase();
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    // Hàm xử lý paragraph
    private void processParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {
        List<XWPFRun> runs = new ArrayList<>(paragraph.getRuns());
        if (runs.isEmpty()) return;

        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null) fullText.append(text);
        }
        String paragraphText = fullText.toString();
        if (paragraphText.isEmpty()) return;

        // ✅ Xử lý riêng cho {{land_items}} trước khi thay thế
        if (paragraphText.contains("{{land_items}}")) {
            String landItems = replacements.get("{{land_items}}");
            if (landItems != null && !landItems.isEmpty()) {
                for (XWPFRun run : runs) {
                    run.setText("", 0); // xóa run cũ
                }

                XWPFRun baseRun = runs.get(0);
                String[] items = landItems.split("\\+");
                for (String item : items) {
                    if (!item.trim().isEmpty()) {
                        XWPFRun runLine = paragraph.createRun();
                        copyStyle(baseRun, runLine);
                        runLine.setText("+ " + item.trim());
                        runLine.addBreak(); // xuống dòng
                    }
                }
            }
            return; // đã xử lý riêng, không cần xử lý tiếp
        }

        // Thay thế toàn bộ đoạn văn
        String replacedText = paragraphText;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            replacedText = replacedText.replace(entry.getKey(), entry.getValue());
        }

        if (replacedText.trim().isEmpty()) {
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

        for (XWPFRun run : runs) {
            run.setText("", 0);
        }

        XWPFRun baseRun = runs.get(0);

        String lvValue = replacements.get("{{lv}}");
        if (lvValue != null && replacedText.contains(lvValue)) {
            int idx = replacedText.indexOf(lvValue);

            if (idx > 0) {
                XWPFRun runNormalBefore = paragraph.createRun();
                copyStyle(baseRun, runNormalBefore);
                runNormalBefore.setText(replacedText.substring(0, idx));
            }

            XWPFRun runBold = paragraph.createRun();
            copyStyle(baseRun, runBold);
            runBold.setBold(true);
            runBold.setText(lvValue);

            if (idx + lvValue.length() < replacedText.length()) {
                XWPFRun runNormalAfter = paragraph.createRun();
                copyStyle(baseRun, runNormalAfter);
                runNormalAfter.setText(replacedText.substring(idx + lvValue.length()));
            }
        } else {
            XWPFRun runNormal = paragraph.createRun();
            copyStyle(baseRun, runNormal);
            runNormal.setText(replacedText);
        }
    }


    private void fillInsertedTable(XWPFTable table, TableRequest tableRequest) {
        if (table == null || tableRequest == null || !tableRequest.isDrawTable()) return;

        int colCount = 3;
        int dataRowCount = tableRequest.getRows().size();
        int totalRows = dataRowCount + 1; // +1 header

        // Căn giữa và set width
        table.setTableAlignment(TableRowAlign.CENTER);
        table.setWidth("8000"); // full A4 width

        // Header row
        XWPFTableRow headerRow = table.getRow(0);
        while (headerRow.getTableCells().size() < colCount) {
            headerRow.addNewTableCell();
        }

        // Tạo thêm hàng dữ liệu
        while (table.getNumberOfRows() < totalRows) {
            XWPFTableRow newRow = table.createRow();
            while (newRow.getTableCells().size() < colCount) {
                newRow.addNewTableCell();
            }
        }

        // Header
        for (int c = 0; c < colCount; c++) {
            XWPFParagraph para = headerRow.getCell(c).getParagraphs().get(0);
            para.setAlignment(ParagraphAlignment.CENTER);
            for (int i = para.getRuns().size() - 1; i >= 0; i--) para.removeRun(i);
            XWPFRun run = para.createRun();
            run.setBold(true);
            run.setFontFamily("Times New Roman");
            run.setFontSize(13);
            run.setText(tableRequest.getHeaders().get(c));
        }

        // Dữ liệu
        for (int r = 0; r < dataRowCount; r++) {
            XWPFTableRow row = table.getRow(r + 1);
            for (int c = 0; c < colCount; c++) {
                XWPFParagraph para = row.getCell(c).getParagraphs().get(0);
                para.setAlignment(ParagraphAlignment.CENTER);
                for (int i = para.getRuns().size() - 1; i >= 0; i--) para.removeRun(i);
                XWPFRun run = para.createRun();
                run.setFontFamily("Times New Roman");
                run.setFontSize(13);
                run.setText(tableRequest.getRows().get(r).get(c));
            }
        }
    }

    private String extractPhuong(String diaChi) {
        if (diaChi == null) return "";
        // Tìm vị trí từ "phường"
        int idx = diaChi.toLowerCase().indexOf("phường");
        if (idx == -1) return "";

        // Cắt chuỗi từ sau chữ "phường"
        String sub = diaChi.substring(idx + "phường".length()).trim();

        // Nếu có dấu phẩy thì lấy trước dấu phẩy
        int commaIdx = sub.indexOf(",");
        if (commaIdx != -1) {
            sub = sub.substring(0, commaIdx).trim();
        }
        System.err.println("sub --> " + sub);
        return sub;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        CreditContractEntity entity = creditContractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với id: " + id));

//        // Xóa file vật lý của avatars
//        if (entity.getAvatars() != null) {
//            entity.getAvatars().forEach(avatar -> {
//                try {
//                    Path path = Paths.get(avatar.getFilePath());
//                    Files.deleteIfExists(path);
//                } catch (IOException e) {
//                    System.err.println("Không thể xóa avatar file: " + avatar.getFilePath() + " - " + e.getMessage());
//                }
//            });
//        }
//
//        // Nếu có metadata file liên quan thì xóa luôn
//        List<FileMetadataEntity> metadataList = fileMetadataRepository.findAll();
//        metadataList.stream()
//                .filter(meta -> meta.getFilePath() != null && meta.getFilePath().contains(String.valueOf(entity.getId())))
//                .forEach(meta -> {
//                    try {
//                        Path path = Paths.get(meta.getFilePath());
//                        System.err.println("path --> "+path);
//                        Files.deleteIfExists(path);
//                    } catch (IOException e) {
//                        System.err.println("Không thể xóa file metadata: " + meta.getFilePath());
//                    }
//                    fileMetadataRepository.delete(meta);
//                });

        // Xóa entity trong DB
        creditContractRepository.delete(entity);
    }


}
