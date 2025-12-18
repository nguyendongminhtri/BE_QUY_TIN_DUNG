package com.example.demo.service.creditcontract;

import com.example.demo.dto.request.ContractRequest;
import com.example.demo.dto.request.TableRequest;
import com.example.demo.mapper.ContractMapper;
import com.example.demo.model.AvatarEntity;
import com.example.demo.model.CreditContractEntity;
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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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

    @Override
    public void deleteById(Long id) {

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

        return fileUrls;
    }

    // 👉 Export: tạo mới hợp đồng và lưu DB
    @Transactional
    public List<String> generateContractFilesExport(ContractRequest request) throws IOException {
        User user = userDetailService.getCurrentUser();
        LocalDate date = LocalDate.parse(request.getContractDate());

// Cách 1: OffsetDateTime
//        LocalDate date = OffsetDateTime.parse(request.getContractDate()).toLocalDate();
        CreditContractEntity entity = new CreditContractEntity();
        contractMapper.mapRequestToEntity(request, entity, user, date);
        contractMapper.processAvatars(request, entity, tempDir, uploadDir, fileMetadataRepository);

        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(generateContractFileExport(request, date, user, "File1.docx"));
        fileUrls.add(generateContractFileExport(request, date, user, "File2.docx"));
        fileUrls.add(generateContractFileExport(request, date, user, "File3.docx"));

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
                Map.entry("{{chv}}", Optional.ofNullable(request.getChoVay()).orElse("")),
                Map.entry("{{lv}}", Optional.ofNullable(request.getLoaiVay()).orElse("")),
                Map.entry("{{day}}", String.format("%02d", date.getDayOfMonth())),
                Map.entry("{{month}}", String.format("%02d", date.getMonthValue())),
                Map.entry("{{year}}", String.valueOf(date.getYear()))
        );

        // Tìm paragraph có placeholder bảng
        for (XWPFParagraph para : new ArrayList<>(doc.getParagraphs())) {
            String text = para.getText();
            if (text != null && text.contains("{{TABLE_PLACEHOLDER}}")) {
                for (int i = para.getRuns().size() - 1; i >= 0; i--) {
                    para.removeRun(i);
                }

                XmlCursor cursor = para.getCTP().newCursor();
                XWPFTable table = doc.insertNewTbl(cursor);

                if (table != null) {
                    fillInsertedTable(table, request.getTableRequest());
                } else {
                    System.err.println("Không tạo được bảng tại {{TABLE_PLACEHOLDER}}");
                }

                // Xóa paragraph placeholder
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

        // Duyệt paragraph ngoài bảng
        for (XWPFParagraph paragraph : new ArrayList<>(doc.getParagraphs())) {
            processParagraph(paragraph, replacements);
        }

        // Duyệt paragraph trong bảng
        for (XWPFTable table : new ArrayList<>(doc.getTables())) {
            for (XWPFTableRow row : new ArrayList<>(table.getRows())) {
                for (XWPFTableCell cell : new ArrayList<>(row.getTableCells())) {
                    for (XWPFParagraph paragraph : new ArrayList<>(cell.getParagraphs())) {
                        processParagraph(paragraph, replacements);
                    }
                }
            }
        }
        for (XWPFParagraph para : doc.getParagraphs()) {
            System.err.println("Text: " + para.getText());
            System.err.println("IndentationFirstLine: " + para.getIndentationFirstLine());
            System.err.println("IndentationLeft: " + para.getIndentationLeft());
            System.err.println("IndentationHanging: " + para.getIndentationHanging());
        }

//
//        // 👉 Reset indent toàn cục cho mọi paragraph
//        for (XWPFParagraph para : new ArrayList<>(doc.getParagraphs())) {
//            para.setIndentationFirstLine(0);
//            para.setIndentationLeft(0);
//            para.setIndentationHanging(0);
//        }
//        for (XWPFTable table : new ArrayList<>(doc.getTables())) {
//            for (XWPFTableRow row : new ArrayList<>(table.getRows())) {
//                for (XWPFTableCell cell : new ArrayList<>(row.getTableCells())) {
//                    for (XWPFParagraph para : new ArrayList<>(cell.getParagraphs())) {
//                        para.setIndentationFirstLine(0);
//                        para.setIndentationLeft(0);
//                        para.setIndentationHanging(0);
//                    }
//                }
//            }
//        }
    }



    private void copyStyle(XWPFRun source, XWPFRun target) {
    if (source.getCTR() != null && source.getCTR().getRPr() != null) {
        target.getCTR().setRPr(source.getCTR().getRPr());
    }
}

    // Hàm xử lý paragraph
    // Hàm xử lý paragraph với reset indent
    // Hàm xử lý paragraph với reset indent + trim text
    private void processParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {
        List<XWPFRun> runs = new ArrayList<>(paragraph.getRuns());
        if (runs.isEmpty()) return;

        // Ghép toàn bộ text
        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null) fullText.append(text);
        }
        String paragraphText = fullText.toString();
        if (paragraphText.isEmpty()) return;

        // Thay thế toàn bộ đoạn văn
        String replacedText = paragraphText;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            replacedText = replacedText.replace(entry.getKey(), entry.getValue());
        }

        // 👉 Loại bỏ tab/space thừa ở đầu và cuối
        replacedText = replacedText.stripLeading().stripTrailing();

        // Nếu sau thay thế rỗng → xóa paragraph
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

        // Xóa toàn bộ run cũ
        for (XWPFRun run : runs) {
            run.setText("", 0);
        }

        // Reset indent để tránh bị thụt dòng
        paragraph.setIndentationFirstLine(0);
        paragraph.setIndentationLeft(0);
        paragraph.setIndentationHanging(0);

        // Run gốc để copy style
        XWPFRun baseRun = runs.get(0);

        // Xử lý riêng cho {{lv}}
        String lvValue = replacements.get("{{lv}}");
        if (lvValue != null && replacedText.contains(lvValue)) {
            int idx = replacedText.indexOf(lvValue);

            // Phần trước {{lv}}
            if (idx > 0) {
                XWPFRun runNormalBefore = paragraph.createRun();
                copyStyle(baseRun, runNormalBefore);
                runNormalBefore.setText(replacedText.substring(0, idx).stripLeading());
            }

            // Phần {{lv}} → bold
            XWPFRun runBold = paragraph.createRun();
            copyStyle(baseRun, runBold);
            runBold.setBold(true);
            runBold.setText(lvValue.strip());

            // Phần sau {{lv}}
            if (idx + lvValue.length() < replacedText.length()) {
                XWPFRun runNormalAfter = paragraph.createRun();
                copyStyle(baseRun, runNormalAfter);
                runNormalAfter.setText(replacedText.substring(idx + lvValue.length()).stripTrailing());
            }
        } else {
            // Nếu không có {{lv}} thì gán toàn bộ vào run thường
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
}
