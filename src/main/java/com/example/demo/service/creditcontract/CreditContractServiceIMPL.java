package com.example.demo.service.creditcontract;

import com.example.demo.dto.request.*;
import com.example.demo.mapper.ContractMapper;
import com.example.demo.model.CreditContractEntity;

import java.math.BigInteger;
import java.text.DecimalFormat;

import com.example.demo.repository.ICreditContractPAVVRepository;
import org.apache.commons.lang3.StringUtils;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import com.example.demo.model.User;
import com.example.demo.repository.ICreditContractRepository;
import com.example.demo.repository.IFileMetadataRepository;
import com.example.demo.security.userprincal.UserDetailService;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
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
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private ICreditContractPAVVRepository creditContractPAVVRepository;
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
        LocalDate dateTC = LocalDate.parse(request.getNgayTheChap());
//        LocalDate dateBD = LocalDate.parse(request.getNgayBaoDam());

        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(generateContractFile(request, date, dateTC, user, "HopDongTinDung.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "HopDongTheChap.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "PhieuBaoDamQSDD.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "GiayDeNghiVayVon.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "DanhMucHoSoChoVay.docx"));
//        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "PhuLucHopDong.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "BienBanKiemTraSauKhiChoVay.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "BienBanXetDuyetChoVay.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "BienBanXacDinhGiaTriTaiSanBaoDam.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "PhuongAnVayVon.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "BaoCaoDeNghiGiaiNganKiemGiayNhanNo.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "BaoCaoThongTinVeNguoiCoLienQuan.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "ThongBao.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "BaoCaoThamDinhVaDeXuatChoVay.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "BaoCaoSuDungVonVay.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, user, "BaoCaoThucTrangTaiChinh.docx"));
        if (Boolean.TRUE.equals(request.getPavvRequest().getVayLai()) && request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)")) {
            fileUrls.add(generateContractFileExport(request, date, null, user, "PhuLucHanMuc.docx", null));
        }
        if (Boolean.TRUE.equals(request.getPavvRequest().getGiaiNganHM()) && request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)")) {
            fileUrls.add(generateContractFileExport(request, date, null, user, "DanhMucBoSungHoSoChoVayHanMuc.docx", null));
            fileUrls.add(generateContractFileExport(request, date, null, user, "PhuLucHopDong.docx", null));
            fileUrls.add(generateContractFileExport(request, date, dateTC, user, "ToTrinhGiaiNganVayHanMuc.docx", null));
        }
        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "GiayGioiThieu.docx", null));
        return fileUrls;
    }

    // 👉 Export: tạo mới hợp đồng và lưu DB
    @Transactional
    public List<String> generateContractFilesExport(ContractRequest request) throws IOException {
        System.err.println("generateContractFilesExport");
        User user = userDetailService.getCurrentUser();
        LocalDate date = LocalDate.parse(request.getContractDate());
        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(generateContractFileExport(request, date, null, user, "HopDongTinDung.docx", null));
//            fileUrls.add(generateContractFileExport(request, date, null, user, "HopDongTheChap.docx"));
//            fileUrls.add(generateContractFileExport(request, date, null, user, "PhieuBaoDamQSDD.docx"));
        fileUrls.add(generateContractFileExport(request, date, null, user, "GiayDeNghiVayVon.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "DanhMucHoSoChoVay.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BienBanKiemTraSauKhiChoVay.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BienBanXetDuyetChoVay.docx", null));
//            fileUrls.add(generateContractFileExport(request, date, null, user, "BienBanXacDinhGiaTriTaiSanBaoDam.docx"));
        fileUrls.add(generateContractFileExport(request, date, null, user, "PhuongAnVayVon.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BaoCaoDeNghiGiaiNganKiemGiayNhanNo.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BaoCaoThongTinVeNguoiCoLienQuan.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "ThongBao.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BaoCaoThamDinhVaDeXuatChoVay.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BaoCaoSuDungVonVay.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BaoCaoThucTrangTaiChinh.docx", null));
        if (Boolean.TRUE.equals(request.getPavvRequest().getVayLai()) && request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)")) {
            fileUrls.add(generateContractFileExport(request, date, null, user, "PhuLucHanMuc.docx", null));
        }
        if (Boolean.TRUE.equals(request.getPavvRequest().getGiaiNganHM()) && request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)")) {
            fileUrls.add(generateContractFileExport(request, date, null, user, "DanhMucBoSungHoSoChoVayHanMuc.docx", null));
            fileUrls.add(generateContractFileExport(request, date, null, user, "PhuLucHopDong.docx", null));
            fileUrls.add(generateContractFileExport(request, date, null, user, "ToTrinhGiaiNganVayHanMuc.docx", null));
        }
        fileUrls.add(generateContractFileExport(request, date, null, user, "GiayGioiThieu.docx", null));
        for (CreditContractTSBDRequest ts : request.getTaiSanArray()) {
            LocalDate ngayTheChapStr = ts.getNgayTheChap();
            System.err.println("ngayTheChapStr -->" + ngayTheChapStr);
            // Export các giấy tờ liên quan tới tài sản này
            fileUrls.add(generateContractFileExport(request, date, ngayTheChapStr, user, "HopDongTheChap.docx", ts));
            fileUrls.add(generateContractFileExport(request, date, ngayTheChapStr, user, "PhieuBaoDamQSDD.docx", ts));
            fileUrls.add(generateContractFileExport(request, date, ngayTheChapStr, user, "BienBanXacDinhGiaTriTaiSanBaoDam.docx", ts));
        }
        CreditContractEntity entity = new CreditContractEntity();
        contractMapper.mapRequestToEntity(request, entity, user, date, null);
        contractMapper.processAvatars(request, entity, tempDir, uploadDir, fileMetadataRepository);
        creditContractRepository.save(entity);
        return fileUrls;
    }


//    // 👉 Export: update hợp đồng đã có
//    @Transactional
//    public List<String> updateContractFilesExport(Long id, ContractRequest request) throws IOException {
//        System.err.println("updateContractFilesExport");
//        User user = userDetailService.getCurrentUser();
//        LocalDate date = LocalDate.parse(request.getContractDate());
//        LocalDate dateTC = LocalDate.parse(request.getNgayTheChap());
////        LocalDate dateBD = LocalDate.parse(request.getNgayBaoDam());
//        // Update entity theo id
//        CreditContractEntity entity = creditContractRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));
//        String soHDTDCu = entity.getSoHopDongTD();
//        LocalDate ngayHDTDCu = entity.getContractDate();
//        if (Boolean.TRUE.equals(request.getPavvRequest().getVayLai())) {
//            Integer reLoanSeq = request.getPavvRequest().getReLoanSequence();
//            System.err.println("reLoanSeq: " + reLoanSeq);
//            System.err.println("So HDTD cu: " + entity.getSoHopDongTD());
//            System.err.println("Ngay ky HDTD cu: " + entity.getContractDate());
//            System.err.println("So HDTD moi: " + request.getSoHopDongTD());
//            System.err.println("Ngay ky HDTD cu: " + request.getContractDate());
//            // Gán giá trị cũ vào TsbdRequest để dùng trong replacePlaceholders
////            if (request.getTsbdRequest() != null) {
////                request.getTsbdRequest().setSoHDTDCu(soHDTDCu);
////                request.getTsbdRequest().setNgayHDTDCu(ngayHDTDCu);
////            }
//            if (reLoanSeq != null) {
//                // ⚠️ Check theo cả reLoanSeq và contractId
//                boolean exists = creditContractPAVVRepository.existsByReLoanSequenceAndCreditContract_Id(reLoanSeq, id);
//                System.err.println("exists --> " + exists);
//                if (!exists) {
////                    // Nếu chưa tồn tại thì tạo mới entity
////                    CreditContractEntity newEntity = new CreditContractEntity();
////                    contractMapper.mapRequestToEntity(request, newEntity, user, date, dateTC, dateBD);
////                    contractMapper.processAvatars(request, newEntity, tempDir, uploadDir, fileMetadataRepository);
////                    creditContractRepository.save(newEntity);
//
//                    return generateContractFilesExport(request);
//                }
//                // Nếu đã tồn tại thì tiếp tục update entity hiện tại
//            }
//        }
//
//        contractMapper.mapRequestToEntity(request, entity, user, date, dateTC);
//        contractMapper.processAvatars(request, entity, tempDir, uploadDir, fileMetadataRepository);
//
//        List<String> fileUrls = new ArrayList<>();
//        // Luôn truyền template gốc, suffix sẽ được xử lý trong generateContractFileExport
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "HopDongTinDung.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "HopDongTheChap.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "PhieuBaoDamQSDD.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "GiayDeNghiVayVon.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "DanhMucHoSoChoVay.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "BienBanKiemTraSauKhiChoVay.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "BienBanXetDuyetChoVay.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "BienBanXacDinhGiaTriTaiSanBaoDam.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "PhuongAnVayVon.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "BaoCaoDeNghiGiaiNganKiemGiayNhanNo.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "BaoCaoThongTinVeNguoiCoLienQuan.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "ThongBao.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "BaoCaoThamDinhVaDeXuatChoVay.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "BaoCaoSuDungVonVay.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "BaoCaoThucTrangTaiChinh.docx"));
//        if (Boolean.TRUE.equals(request.getPavvRequest().getVayLai()) && request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)")) {
//            fileUrls.add(generateContractFileExport(request, date, dateTC, user, "PhuLucHanMuc.docx"));
//        }
//        if (Boolean.TRUE.equals(request.getPavvRequest().getGiaiNganHM()) && request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)")) {
//            fileUrls.add(generateContractFileExport(request, date, dateTC, user, "DanhMucBoSungHoSoChoVayHanMuc.docx"));
//            fileUrls.add(generateContractFileExport(request, date, dateTC, user, "PhuLucHopDong.docx"));
//            fileUrls.add(generateContractFileExport(request, date, dateTC, user, "ToTrinhGiaiNganVayHanMuc.docx"));
//        }
//        fileUrls.add(generateContractFileExport(request, date, dateTC, user, "GiayGioiThieu.docx"));
//        creditContractRepository.save(entity);
//        return fileUrls;
//    }

    @Transactional
    public List<String> updateContractFilesExport(Long id, ContractRequest request) throws IOException {
        User user = userDetailService.getCurrentUser();
        LocalDate date = LocalDate.parse(request.getContractDate());

        // Lấy entity theo id
        CreditContractEntity entity = creditContractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        // Map lại dữ liệu hợp đồng + toàn bộ tài sản
        contractMapper.mapRequestToEntity(request, entity, user, date, null);
        contractMapper.processAvatars(request, entity, tempDir, uploadDir, fileMetadataRepository);

        List<String> fileUrls = new ArrayList<>();

        // Giấy tờ chung (1 file)
        fileUrls.add(generateContractFileExport(request, date, null, user, "HopDongTinDung.docx", null));
//        fileUrls.add(generateContractFileExport(request, date, null, user, "HopDongTheChap.docx"));
//        fileUrls.add(generateContractFileExport(request, date, null, user, "PhieuBaoDamQSDD.docx"));
        fileUrls.add(generateContractFileExport(request, date, null, user, "GiayDeNghiVayVon.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "DanhMucHoSoChoVay.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BienBanKiemTraSauKhiChoVay.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BienBanXetDuyetChoVay.docx", null));
//        fileUrls.add(generateContractFileExport(request, date, null, user, "BienBanXacDinhGiaTriTaiSanBaoDam.docx"));
        fileUrls.add(generateContractFileExport(request, date, null, user, "PhuongAnVayVon.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BaoCaoDeNghiGiaiNganKiemGiayNhanNo.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BaoCaoThongTinVeNguoiCoLienQuan.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "ThongBao.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BaoCaoThamDinhVaDeXuatChoVay.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BaoCaoSuDungVonVay.docx", null));
        fileUrls.add(generateContractFileExport(request, date, null, user, "BaoCaoThucTrangTaiChinh.docx", null));
        // Giấy tờ theo từng tài sản (nhiều file)
        for (CreditContractTSBDRequest ts : request.getTaiSanArray()) {
            LocalDate dateTC = ts.getNgayTheChap();
            fileUrls.add(generateContractFileExport(request, date, dateTC, user, "HopDongTheChap.docx", ts));
            fileUrls.add(generateContractFileExport(request, date, dateTC, user, "PhieuBaoDamQSDD.docx", ts));
            fileUrls.add(generateContractFileExport(request, date, dateTC, user, "BienBanXacDinhGiaTriTaiSanBaoDam.docx", ts));
        }

        // Các giấy tờ khác theo điều kiện
        if (Boolean.TRUE.equals(request.getPavvRequest().getVayLai()) && request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)")) {
            fileUrls.add(generateContractFileExport(request, date, null, user, "PhuLucHanMuc.docx", null));
        }
        if (Boolean.TRUE.equals(request.getPavvRequest().getGiaiNganHM()) && request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)")) {
            fileUrls.add(generateContractFileExport(request, date, null, user, "DanhMucBoSungHoSoChoVayHanMuc.docx", null));
            fileUrls.add(generateContractFileExport(request, date, null, user, "PhuLucHopDong.docx", null));
            fileUrls.add(generateContractFileExport(request, date, null, user, "ToTrinhGiaiNganVayHanMuc.docx", null));
        }

        creditContractRepository.save(entity);
        return fileUrls;
    }


    // 👉 Hàm generate file (preview)
    private String generateContractFile(ContractRequest request, LocalDate date, LocalDate dateTC, User user, String templateName) throws IOException {
        try (InputStream is = new ClassPathResource("templates/" + templateName).getInputStream();
             XWPFDocument doc = new XWPFDocument(is)) {

            replacePlaceholders(doc, request, date, dateTC, null);
            fixTablesEnsureParagraphs(doc);
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

    // 👉 Helper: sinh một file duy nhất từ template
    private String generateContractFileExport(@NotNull ContractRequest request,
                                              @NotNull LocalDate date,
                                              @NotNull LocalDate dateTC,
                                              @NotNull User user,
                                              @NotNull String templateName,
                                              @Nullable CreditContractTSBDRequest ts) throws IOException {
        // Luôn load template gốc
        try (InputStream is = new ClassPathResource("templates/" + templateName).getInputStream();
             XWPFDocument doc = new XWPFDocument(is)) {

            replacePlaceholders(doc, request, date, dateTC, ts);
            fixTablesEnsureParagraphs(doc);
            expandTablesFullWidth(doc);

            String fileName = templateName.replace(".docx", "")
                    + "_export_" + user.getId()
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

    Map<String, String> replacements = new HashMap<>();

    private void replacePlaceholders(XWPFDocument doc, ContractRequest request, LocalDate date, LocalDate dateTC, @Nullable CreditContractTSBDRequest ts) {
        System.err.println("request --> " + request);
        System.err.println("date ::::" + date);
        System.err.println("date ::::" + dateToWords(date));
        System.err.println("dateTC ::::" + dateTC);
//        LocalDate dateKT = date.plusYears(Long.parseLong(request.getThoiHanVay()));
//        System.err.println("dateKT ::::" + dateKT);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        String ngayKetThuc = dateKT.format(formatter);

        LocalDate dateKT = date;
        String thv = request.getThoiHanVay();
        if (thv != null && !thv.isBlank()) {
            try {
                long years = Long.parseLong(thv.trim());
                dateKT = date.plusYears(years);
            } catch (NumberFormatException e) {
                System.err.println("Không parse được thoiHanVay: " + thv);
            }
        }
        String ngayKetThuc = dateKT.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        //TẠO END DATE
//        String thv = request.getThoiHanVay();
        LocalDate endDate = date; // mặc định là ngày bắt đầu

        if (thv != null && !thv.isBlank()) {
            try {
                int years = Integer.parseInt(thv.trim()); // parse số năm
                endDate = date.plusYears(years);
            } catch (NumberFormatException e) {
                System.err.println("Không parse được thv: " + thv);
            }
        }
        // Khởi tạo biến tiền số
        long tienSo = parseLongSafe(request.getTienSo());
// Thêm placeholder mới
        safePutReplacement("{{endDate}}", endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        String soGiaiNgan = "01";
        if (Boolean.TRUE.equals(request.getPavvRequest().getGiaiNganHM()) && request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)")) {
            soGiaiNgan = Optional.ofNullable(request.getPavvRequest().getSoGiaiNgan()).orElse("01");
            String ngayGiaiNganStr = String.valueOf(request.getPavvRequest().getNgayGiaiNgan()); // dữ liệu từ frontend
            LocalDate ngayGiaiNgan = LocalDate.parse(ngayGiaiNganStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

// Tách thành các phần
            String dayGN = String.format("%02d", ngayGiaiNgan.getDayOfMonth());
            String monthGN = String.format("%02d", ngayGiaiNgan.getMonthValue());
            String yearGN = String.valueOf(ngayGiaiNgan.getYear());
// Đưa vào placeholders
            safePutReplacement("{{dayGN}}", dayGN);
            safePutReplacement("{{monthGN}}", monthGN);
            safePutReplacement("{{yearGN}}", yearGN);
        }
        safePutReplacement("{{soGiaiNgan}}", soGiaiNgan);
//        String formattedTienSo = df.format(tienSo);
        // Các placeholder mặc định
//       safePutReplacement("{{gd}}", Optional.ofNullable(request.getNguoiDaiDien()).orElse(""));
        safePutReplacement("{{dateTextWords}}", dateToWords(date));
        safePutReplacement("{{shdtd}}", Optional.ofNullable(request.getSoHopDongTD()).orElse(""));
        safePutReplacement("{{gtkh}}", Optional.ofNullable(request.getGtkh()).orElse(""));
        safePutReplacement("{{gtkht}}", Optional.ofNullable(request.getGtkh().toLowerCase()).orElse(""));
        safePutReplacement("{{kh}}", Optional.ofNullable(request.getTenKhachHang()).orElse(""));
        safePutReplacement("{{nskh}}", Optional.ofNullable(request.getNamSinhKhachHang()).orElse(""));
        safePutReplacement("{{sdtkh}}", Optional.ofNullable(request.getPhoneKhachHang()).orElse(""));
        safePutReplacement("{{sttv}}", Optional.ofNullable(request.getSoTheThanhVienKhachHang()).orElse(""));
        safePutReplacement("{{cccdkh}}", Optional.ofNullable(request.getCccdKhachHang()).orElse(""));
        safePutReplacement("{{nckh}}", Optional.ofNullable(request.getNgayCapCCCDKhachHang()).orElse(""));
        safePutReplacement("{{nccccdkh}}", Optional.ofNullable(request.getNoiCapCCCDKhachHang()).orElse(""));
        safePutReplacement("{{ttkh}}", Optional.ofNullable(request.getDiaChiThuongTruKhachHang()).orElse(""));
        safePutReplacement("{{tdp}}", Optional.ofNullable(request.getDiaChiThuongTruKhachHang().split(",")[0].trim()).orElse(""));
        safePutReplacement("{{gtnt}}", Optional.ofNullable(request.getGtnt()).orElse(""));
        safePutReplacement("{{gtntt}}", Optional.ofNullable(request.getGtnt().toLowerCase()).orElse(""));
        safePutReplacement("{{ntkh}}", Optional.ofNullable(request.getTenNguoiThan()).orElse(""));
        safePutReplacement("{{nsnt}}", Optional.ofNullable(request.getNamSinhNguoiThan()).orElse(""));
        safePutReplacement("{{cccdnt}}", Optional.ofNullable(request.getCccdNguoiThan()).orElse(""));
        safePutReplacement("{{ncnt}}", Optional.ofNullable(request.getNgayCapCCCDNguoiThan()).orElse(""));
        safePutReplacement("{{nccccdnt}}", Optional.ofNullable(request.getNoiCapCCCDNguoiThan()).orElse(""));
        safePutReplacement("{{ttnt}}", Optional.ofNullable(request.getDiaChiThuongTruNguoiThan()).orElse(""));
        safePutReplacement("{{qh}}", Optional.ofNullable(request.getQuanHe()).orElse(""));
        safePutReplacement("{{tienso}}", Optional.ofNullable(request.getTienSo()).orElse(""));
        safePutReplacement("{{tc}}", Optional.ofNullable(request.getTienChu()).orElse(""));
        safePutReplacement("{{mdvay}}", Optional.ofNullable(request.getMuchDichVay()).orElse(""));
        safePutReplacement("{{hm}}", Optional.ofNullable(request.getHanMuc() + " tháng").orElse(""));
        safePutReplacement("{{sbbxdcv}}", Optional.ofNullable(request.getSoBBXetDuyetChoVay()).orElse(""));
        safePutReplacement("{{ls}}", Optional.ofNullable(request.getLaiSuat()).orElse(""));
        safePutReplacement("{{nkt}}", Optional.ofNullable(ngayKetThuc).orElse(""));
        safePutReplacement("{{chv}}", Optional.ofNullable(request.getChoVay()).orElse(""));
        // xử lý taiSanBlock như cũ
        if (request.getTaiSanArray() != null && !request.getTaiSanArray().isEmpty()) {
            CreditContractTSBDRequest tsFirst = request.getTaiSanArray().get(0);
            String nguoiNhanThongBao = Optional.ofNullable(tsFirst.getDungTenBiaDo1()).orElse("");
            if (Boolean.TRUE.equals(tsFirst.getCheckDongSoHuu())) {
                nguoiNhanThongBao += " và ";
                nguoiNhanThongBao += Optional.ofNullable(tsFirst.getDungTenBiaDo2()).orElse("");
            }
            safePutReplacement("{{nguoiNTB}}", nguoiNhanThongBao);
            StringBuilder giayChungNhanBuilder = new StringBuilder();

            for (int i = 0; i < request.getTaiSanArray().size(); i++) {
                CreditContractTSBDRequest tsbd = request.getTaiSanArray().get(i);

                if (i > 0) {
                    giayChungNhanBuilder.append("; Và ");
                }

                giayChungNhanBuilder.append(Optional.ofNullable(tsbd.getNoiDungNgoaiBia()).orElse(""))
                        .append(" số: ")
                        .append(Optional.ofNullable(tsbd.getSerial()).orElse(""))
                        .append(". ")
                        .append(Optional.ofNullable(tsbd.getNoiDungVaoSo()).orElse(""))
                        .append(" do ")
                        .append(Optional.ofNullable(tsbd.getNoiCapSo()).orElse(""))
                        .append(". Cấp ngày: ")
                        .append(Optional.ofNullable(tsbd.getNgayCapSo()).orElse(""));
            }

            safePutReplacement("{{giayChungNhanBlock}}", giayChungNhanBuilder.toString());
            StringBuilder sb = new StringBuilder();
            long tongTatCaTaiSan = 0;

            for (int i = 0; i < request.getTaiSanArray().size(); i++) {
                CreditContractTSBDRequest tsbd = request.getTaiSanArray().get(i);

                sb.append(buildTaiSanBlock(tsbd)).append("\n");

                if (i < request.getTaiSanArray().size() - 1) {
                    sb.append("Và\n\n");
                }

                try {
                    String tongStr = tsbd.getTongTaiSanBD();
                    if (tongStr != null && !tongStr.isEmpty()) {
                        tongTatCaTaiSan += parseLongSafe(tongStr);
                    }
                } catch (NumberFormatException e) {
                    // bỏ qua nếu không parse được
                }

                // 👉 xử lý placeholder riêng cho nhà ở cố định
                if (StringUtils.isNotBlank(tsbd.getDienTichTS()) || StringUtils.isNotBlank(tsbd.getKetCauXayDung())) {
                    String nhaOCoDinh = "- Nhà ở cố định: "
                            + Optional.ofNullable(tsbd.getDienTichTS()).orElse("") + " m²; Loại nhà: "
                            + Optional.ofNullable(tsbd.getKetCauXayDung()).orElse("")
                            + " (Không được định giá)";
                    safePutReplacement("{{nhaOCoDinh}}", nhaOCoDinh);
                    // phiên bản rút gọn, bỏ "(Không được định giá)"
                    String nhaOCoDinhShort = "- Nhà ở cố định: "
                            + Optional.ofNullable(tsbd.getDienTichTS()).orElse("") + " m²; Loại nhà: "
                            + Optional.ofNullable(tsbd.getKetCauXayDung()).orElse("");
                    safePutReplacement("{{nhaOCoDinhShort}}", nhaOCoDinhShort);
                }
            }
            double phanTramTyLe = Math.round(((double) tienSo / tongTatCaTaiSan) * 1000.0) / 10.0;
            safePutReplacement("{{tongTsbds}}", formatCurrency(tongTatCaTaiSan));
            safePutReplacement("{{tongTsbdc}}", numberToVietnameseWordsMoney(tongTatCaTaiSan));
            safePutReplacement("{{phanTramTyLe}}", String.valueOf(phanTramTyLe));
        }


        safePutReplacement("{{tgvv}}", Optional.ofNullable(request.getHanMuc()).orElse(""));
        safePutReplacement("{{nguoiChuyenKhoan}}", Optional.ofNullable(request.getPavvRequest().getNguoiChuyenKhoan()).orElse(""));
        if (Boolean.TRUE.equals(request.getPavvRequest().getVayLai())) {
            safePutReplacement("{{tieuDeBCTD}}", "KHOẢN VAY");
        } else {
            safePutReplacement("{{tieuDeBCTD}}", "VÀ ĐỀ XUẤT CHO VAY");
        }
        if (ts != null) {
            safePutReplacement("{{ndnb}}", Optional.ofNullable(ts.getNoiDungNgoaiBia()).orElse(""));
            safePutReplacement("{{shdtc}}", Optional.ofNullable(ts.getSoHopDongTheChapQSDD()).orElse(""));
            if (ts.getNgayTheChap() != null) {
                safePutReplacement("{{dayTC}}", String.format("%02d", ts.getNgayTheChap().getDayOfMonth()));
                safePutReplacement("{{monthTC}}", String.format("%02d", ts.getNgayTheChap().getMonthValue()));
                safePutReplacement("{{yearTC}}", String.valueOf(ts.getNgayTheChap().getYear()));
            }
            safePutReplacement("{{ngayKyTC}}", ts.getNgayTheChap() != null
                    ? ts.getNgayTheChap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "");
            System.err.println("noi Dung:::" + "Biên bản xác định lại giá trị tài sản bổ sung cho HĐTC số: " + ts.getSoHopDongTheChapQSDD()
                    + " Ngày " + String.format("%02d", ts.getNgayTheChap().getDayOfMonth()) + " tháng " + String.format("%02d", ts.getNgayTheChap().getMonthValue()) + " năm " + String.valueOf(ts.getNgayTheChap().getYear()));
            safePutReplacement("{{noiDung}}", "Biên bản xác định lại giá trị tài sản bổ sung cho HĐTC số: " + ts.getSoHopDongTheChapQSDD()
                    + " Ngày " + String.format("%02d", ts.getNgayTheChap().getDayOfMonth()) + " tháng " + String.format("%02d", ts.getNgayTheChap().getMonthValue()) + " năm " + String.valueOf(ts.getNgayTheChap().getYear()));
            safePutReplacement("{{gtkhbdt}}", Optional.of(ts.getGioiTinhDungTenBiaDo1().toLowerCase()).orElse(""));
            safePutReplacement("{{khbd}}", Optional.ofNullable(ts.getDungTenBiaDo1()).orElse(""));
            if (Boolean.TRUE.equals(ts.getCheckCMNDDungTenBiaDo1())) {
                safePutReplacement("{{cmnd1}}", ts.getCmndDungTenBiaDo1());
            } else {
                safePutReplacement("{{cmnd1}}", "");
            }

            if (Boolean.TRUE.equals(ts.getCheckNgayCapCCCDTruocDayDungTenBiaDo1())) {
                safePutReplacement("{{ncbd1}}", "CC/CCCD Số: " + ts.getCccdDungTenBiaDo1() + ";" +
                        " Ngày cấp: " + ts.getNgayCapCCCDTruocDayDungTenBiaDo1() + ";" +
                        "(Cấp lại ngày: " + ts.getNgayCapCCCDDungTenBiaDo1() + ")");
            } else {
                safePutReplacement("{{ncbd1}}", "CC/CCCD Số: " + ts.getCccdDungTenBiaDo1() + ";" +
                        " Ngày cấp: " + ts.getNgayCapCCCDDungTenBiaDo1() + ";");
            }

            safePutReplacement("{{noiCapbd1}}", "Nơi cấp: " + ts.getNoiCapCCCDDungTenBiaDo1());

            if (Boolean.TRUE.equals(ts.getCheckCMNDDungTenBiaDo2())) {
                safePutReplacement("{{cmnd2}}", ts.getCmndDungTenBiaDo2());
            } else {
                safePutReplacement("{{cmnd2}}", "");
            }

            if (Boolean.TRUE.equals(ts.getCheckTaiSanGanLienVoiDat())) {
                safePutReplacement("{{dienTichTS}}", Optional.ofNullable(ts.getDienTichTS()).orElse(""));
                safePutReplacement("{{ketCauXayDung}}", Optional.ofNullable(ts.getKetCauXayDung()).orElse(""));
                safePutReplacement("{{loaiNha}}", Optional.ofNullable(ts.getLoaiNha()).orElse(""));
            } else {
                safePutReplacement("{{dienTichTS}}", "0");
                safePutReplacement("{{ketCauXayDung}}", "0");
                safePutReplacement("{{loaiNha}}", "0");
            }
            // Giả sử đang trong vòng lặp for (CreditContractTSBDRequest ts : request.getTaiSanArray())

            if (Boolean.TRUE.equals(ts.getCheckNguonGocSuDung())) {
                safePutReplacement("{{ngsd}}", "Nguồn gốc sử dụng: " + Optional.ofNullable(ts.getNguonGocSuDung()).orElse(""));
            } else {
                safePutReplacement("{{ngsd}}", "");
            }
            if (Boolean.TRUE.equals(ts.getCheckGhiChu())) {
                safePutReplacement("{{gc}}", Optional.ofNullable(ts.getGhiChu()).orElse(""));
//            safePutReplacement("{{gtqsdd}}", formattedGtqsdd); // giá trị quyền sử dụng đất, bạn vẫn tính riêng
            } else {
                safePutReplacement("{{gc}}", "");
            }
// Người đứng tên bìa đỏ 1
            safePutReplacement("{{khbd}}", Optional.ofNullable(ts.getDungTenBiaDo1()).orElse(""));
            safePutReplacement("{{ndnb}}", Optional.ofNullable(ts.getNoiDungNgoaiBia()).orElse(""));
            safePutReplacement("{{gtkhbd}}", Optional.ofNullable(ts.getGioiTinhDungTenBiaDo1()).orElse(""));
            safePutReplacement("{{gtkhbdt}}", Optional.ofNullable(ts.getGioiTinhDungTenBiaDo1()).map(String::toLowerCase).orElse(""));
            safePutReplacement("{{nskhbd}}", Optional.ofNullable(ts.getNamSinhDungTenBiaDo1()).orElse(""));
            safePutReplacement("{{cccdkhbd}}", Optional.ofNullable(ts.getCccdDungTenBiaDo1()).orElse(""));
            safePutReplacement("{{sdtkhbd}}", Optional.ofNullable(ts.getPhoneDungTenBiaDo1()).orElse(""));
            safePutReplacement("{{ngckhbd}}", Optional.ofNullable(ts.getNgayCapCCCDDungTenBiaDo1()).orElse(""));
            safePutReplacement("{{nccccdkhbd}}", Optional.ofNullable(ts.getNoiCapCCCDDungTenBiaDo1()).orElse(""));
            safePutReplacement("{{dckhbd}}", Optional.ofNullable(ts.getDiaChiThuongTruDungTenBiaDo1()).orElse(""));
            if (Boolean.TRUE.equals(ts.getCheckDiaChiThuongTruDungTenBiaDo1())) {
                safePutReplacement("{{dcdtbd1}}", ts.getDiaChiThuongTruDungTenBiaDo1());
                safePutReplacement("{{dcdtbd1s}}", "(nay là: " + ts.getDiaChiThuongTruDungTenBiaDo1() + ")");
            } else {
                safePutReplacement("{{dcdtbd1}}", "Địa chỉ thường trú: " + ts.getDiaChiThuongTruDungTenBiaDo1() + ".");
                safePutReplacement("{{dcdtbd1s}}", "");
            }
// Người đứng tên bìa đỏ 2
            safePutReplacement("{{ntbd}}", Optional.ofNullable(ts.getDungTenBiaDo2()).orElse(""));
            safePutReplacement("{{gtntbd}}", Optional.ofNullable(ts.getGioiTinhDungTenBiaDo2()).orElse(""));
            safePutReplacement("{{gtntbdt}}", Optional.ofNullable(ts.getGioiTinhDungTenBiaDo2()).map(String::toLowerCase).orElse(""));
            safePutReplacement("{{cccdntbd}}", Optional.ofNullable(ts.getCccdDungTenBiaDo2()).orElse(""));
            safePutReplacement("{{ngcntbd}}", Optional.ofNullable(ts.getNgayCapCCCDDungTenBiaDo2()).orElse(""));
            safePutReplacement("{{nccccdntbd}}", Optional.ofNullable(ts.getNoiCapCCCDDungTenBiaDo2()).orElse(""));
            safePutReplacement("{{dcntbd}}", Optional.ofNullable(ts.getDiaChiThuongTruDungTenBiaDo2()).orElse(""));
            safePutReplacement("{{nsntbd}}", Optional.ofNullable(ts.getNamSinhDungTenBiaDo2()).orElse(""));
            safePutReplacement("{{land_items}}", Optional.ofNullable(ts.getLandItems()).orElse(""));
            //CUỐI TẠO END DATE
            System.err.println("===============DUNG TEN BI DO 2 ========================");
            safePutReplacement("{{ntbdd1}}", "Và " + ts.getGioiTinhDungTenBiaDo2().toLowerCase() + ": " + ts.getDungTenBiaDo2() + "; Sinh ngày: " + ts.getNamSinhDungTenBiaDo2() + ".");
            safePutReplacement("{{ntbd}}", ts.getDungTenBiaDo2());
            safePutReplacement("{{ntbdTB}}", ts.getDungTenBiaDo2());
//           safePutReplacement("{{ntbdd2}}", "CC/CCCD số: " + request.getCccdDungTenBiaDo2() + "; Ngày cấp: " + request.getNgayCapCCCDDungTenBiaDo2() + "; Nơi cấp: " + request.getNoiCapCCCDDungTenBiaDo2() + ".");
            if (Boolean.TRUE.equals(ts.getCheckDiaChiThuongTruDungTenBiaDo2())) {
                safePutReplacement("{{ntbdd3}}", ts.getDiaChiThuongTruDungTenBiaDo2());
                safePutReplacement("{{ntbdd3s}}", "(nay là: " + ts.getDiaChiThuongTruDungTenBiaDo2());
            } else {
                safePutReplacement("{{ntbdd3}}", "Cùng địa chỉ thường trú: " + ts.getDiaChiThuongTruDungTenBiaDo1() + ".");
                safePutReplacement("{{dcdtbd1}}", "");
                safePutReplacement("{{ntbdd3s}}", "");
            }
            safePutReplacement("{{ntbdd4}}", "3.6. Họ và tên đầy đủ đối với cá nhân/tên đầy đủ đối với tổ chức: (viết chữ IN HOA)");
            safePutReplacement("{{ntbdd5}}", "Sinh ngày: " + ts.getNamSinhDungTenBiaDo2());
            safePutReplacement("{{ntbdd6}}", "3.7. Địa chỉ thường trú:  " + ts.getDiaChiThuongTruDungTenBiaDo2());
            safePutReplacement("{{ntbdd7}}", "3.8. Giấy tờ xác định tư cách pháp lý: ");
            safePutReplacement("{{ntbdd8}}", "☑ Chứng minh nhân dân/Căn cước công dân/Chứng minh quân đội");
            safePutReplacement("{{ntbdd9}}", "□ Hộ chiếu        □ Thẻ thường trú        □ Mã số thuế");
            safePutReplacement("{{ntbdd10}}", "CC/CCCD Số: " + ts.getCccdDungTenBiaDo2() + "; Ngày cấp: " + ts.getNgayCapCCCDDungTenBiaDo2() + ";");
            safePutReplacement("{{ntbdd10s}}", "Nơi cấp: " + ts.getNoiCapCCCDDungTenBiaDo2() + ";");
            safePutReplacement("{{ntbdd11}}", "3.9. Thuộc đối tượng không phải nộp phí đăng ký □");
            safePutReplacement("{{ntbdd12}}", "3.10. Số điện thoại (nếu có):…..Fax (nếu có):……Thư điện tử (nếu có):………………..");
            if (Boolean.TRUE.equals(ts.getCheckNgayCapCCCDTruocDayDungTenBiaDo2())) {
                safePutReplacement("{{ncbd2}}", "CC/CCCD Số: " + ts.getCccdDungTenBiaDo2() + ";" + " Ngày cấp: " + ts.getNgayCapCCCDTruocDayDungTenBiaDo2() + ";" + "(Cấp lại ngày: " + ts.getNgayCapCCCDDungTenBiaDo2() + ")");
            } else {
                safePutReplacement("{{ncbd2}}", "CC/CCCD Số: " + ts.getCccdDungTenBiaDo2() + ";" + " Ngày cấp: " + ts.getNgayCapCCCDDungTenBiaDo2() + ";");
            }
            safePutReplacement("{{noiCapbd2}}", "Nơi cấp: " + ts.getNoiCapCCCDDungTenBiaDo2());
            System.err.println("===============END DUNG TEN BI DO 2 ========================");
            if (request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN")) {
                safePutReplacement("{{loaivay}}", "Cho vay ngắn hạn");
            } else if (request.getLoaiVay().equalsIgnoreCase("TRUNG HẠN")) {
                safePutReplacement("{{loaivay}}", "Cho vay trung hạn");
            }
            if (ts.getCheckNguoiMangTenBiaDo()) {
                safePutReplacement("{{ndtbd}}", ts.getNguoiMangTen());
            } else {
                String nguoiMangTen = capitalizeWords(ts.getDungTenBiaDo1());
                if (ts.getCheckChiMangTenNguoi2()
                        && !ts.getCheckChiMangTenNguoi1()) {
                    // chỉ người 2
                    nguoiMangTen = capitalizeWords(ts.getDungTenBiaDo2());
                } else if (ts.getCheckChiMangTenNguoi2()
                        && ts.getCheckChiMangTenNguoi1()) {
                    // cả người 1 và người 2
                    nguoiMangTen = capitalizeWords(ts.getDungTenBiaDo1()) + " và " + capitalizeWords(ts.getDungTenBiaDo2());
                }
                System.err.println("nguoiMangTen: " + nguoiMangTen);
                safePutReplacement("{{ndtbd}}", nguoiMangTen);
            }
            safePutReplacement("{{seri}}", Optional.ofNullable(ts.getSerial()).orElse(""));
            safePutReplacement("{{nc}}", Optional.ofNullable(ts.getNoiCapSo()).orElse(""));
            safePutReplacement("{{ngc}}", Optional.ofNullable(ts.getNgayCapSo()).orElse(""));
            safePutReplacement("{{vsmt}}", Optional.ofNullable(ts.getNoiDungVaoSo()).orElse(""));
            safePutReplacement("{{std}}", Optional.ofNullable(ts.getSoThuaDat()).orElse(""));
            safePutReplacement("{{sbd}}", Optional.ofNullable(ts.getSoBanDo()).orElse(""));
            safePutReplacement("{{dctd}}", Optional.ofNullable(ts.getDiaChiThuaDat()).orElse(""));
            safePutReplacement("{{dt}}", Optional.ofNullable(ts.getDienTichDatSo()).orElse(""));
            safePutReplacement("{{dtc}}", Optional.ofNullable(ts.getDienTichDatChu()).orElse(""));
            safePutReplacement("{{htsd}}", Optional.ofNullable(ts.getHinhThucSuDung()).orElse(""));
            if (ts.getCheckMucDich()) {
                safePutReplacement("{{mdsd}}", Optional.of("Mục đích sử dụng: " + ts.getMuchDichSuDung()).orElse(""));
            } else {
                safePutReplacement("{{mdsd}}", "");
            }

            safePutReplacement("{{thsd}}", Optional.ofNullable(ts.getThoiHanSuDung()).orElse(""));
            long gtqsdd = 0L;
            if (ts.getTable3() != null && ts.getTable3().getGiaTriQuyenSuDungDat() != null) {
                gtqsdd = ts.getTable3().getGiaTriQuyenSuDungDat();
            }
            String formattedGtqsdd = formatCurrency(gtqsdd);
            safePutReplacement("{{gtqsdd}}", formattedGtqsdd);
            safePutReplacement("{{tsbds}}", Optional.ofNullable(ts.getTongTaiSanBD()).orElse(""));
            safePutReplacement("{{tsbdc}}", Optional.ofNullable(ts.getTongTaiSanBDChu()).orElse(""));
        }
        if (request.getLoaiVay().equalsIgnoreCase("han_muc")) {
            safePutReplacement("{{lvt}}", Optional.of("Ngắn hạn").orElse(""));
            safePutReplacement("{{slv}}", "Hạn mức");
            safePutReplacement("{{thvGNN}}", "Thời hạn duy trì hạn mức: " + request.getThoiHanVay() + " năm (kể từ ngày " + String.format("%02d", date.getDayOfMonth()) + "/" + String.format("%02d", date.getMonthValue()) + "/" + date.getYear() + " đến hết ngày " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")");
            safePutReplacement("{{tienGNN}}", "Tổng hạn mức tín dụng được cấp:  " + request.getTienSo() + " đồng.");
            safePutReplacement("{{vongQuayGNN}}", "- Vòng quay vốn lưu động dự kiến một năm:  1,2 Vòng/năm.");
            System.err.println("get::" + replacements.get("{{slv}}"));
            safePutReplacement("{{ms1t}}", "Phương thức cho vay: Cho vay theo hạn mức");
            safePutReplacement("{{ms1d}}", "");
            safePutReplacement("{{ms2t}}", "Hạn mức cho vay:");
            safePutReplacement("{{ms2d}}", "Bên A cam kết cho bên B vay các khoản cấp tín dụng bằng đồng Việt Nam với hạn mức cho vay là: " + request.getTienSo() + " đồng, (Bằng chữ: " + request.getTienChu() + " )");
            if (Boolean.TRUE.equals(request.getPavvRequest().getVayLai())) {
                CreditContractPAVVRequest pavvRequest = request.getPavvRequest();
                safePutReplacement("{{ms2e}}", "Hạn mức cho vay bao gồm các khoản nợ được điều chỉnh bởi Hợp đồng cho vay hạn mức số " + pavvRequest.getSoHDTDCu() + "/HĐTD, ký ngày " + pavvRequest.getNgayHDTDCu() + " giữa bên A (Bên cho vay) và bên B (Bên vay vốn) theo liệt kê chi tiết tại Phụ Lục " + request.getPavvRequest().getReLoanSequence() + " Hợp đồng này. ");
                safePutReplacement("{{soHDTDcu}}", pavvRequest.getSoHDTDCu());
                safePutReplacement("{{ngayHDTDcu}}", pavvRequest.getNgayHDTDCu());
            } else {
                safePutReplacement("{{ms2e}}", "Hạn mức cho vay này bao gồm các khoản nợ được điều chỉnh bởi hợp đồng tín dụng hạn mức được ký kêt giữa bên A và bên B theo liệt kê chi tiết tại Phụ lục Hợp đồng này, đồng thời thống nhất việc xử lý số dư nợ vay được liệt kê theo nguyên tắc tham chiếu chi tiết kèm theo.");
            }
            safePutReplacement("{{ms2f}}", "Trong thời hạn duy trì hạn mức tín dụng Bên B được Bên A xem xét cho vay, nhưng dư nợ tại bất kỳ một thời điểm nào cũng không vượt quá hạn mức tín dụng tại khoản 1, Điều này.");
            safePutReplacement("{{ms3}}", "Mục đích sử dụng tiền vay: " + request.getMuchDichVay());
            safePutReplacement("{{ms4}}", "Thời hạn duy trì hạn mức: " + request.getHanMuc() + ", kể từ ngày ký thỏa thuận Hợp đồng tín dụng này. Trong khoảng thời gian này Bên B được đề nghị Bên A cấp tín dụng phù hợp với mục đích sử dụng vốn và có thể đề nghị giải ngân một lần hoặc nhiều lần trong hạn mức nêu tại Hợp đồng tín dụng này. Hết thời hạn duy trì hạn mức hợp đồng này, bên A không có nghĩa vụ giải ngân bất kỳ khoản vay nợ nào");
            safePutReplacement("{{ms5}}", "5. Một năm ít nhất một lần bên A có trách nhiệm xem xét, xác định lại hạn mức cho vay tối đa và thời gian duy trì hạn mức Hợp đồng này.");
            safePutReplacement("{{ms6t}}", "6. Thời hạn cho vay: Từng khoản cấp tín dụng được xác định cụ thể trên từng giấy nhận nợ, mỗi giấy nhận nợ có thời gian cho vay khác nhau và được Bên A xác định vào chu kỳ sản xuất kinh doanh, khả năng trả nợ của Bên B và không vượt quá 10 tháng hoặc không vượt quá một thời hạn khác do Bên A xác định trong từng thời kỳ.");
            safePutReplacement("{{ms6d}}", "Thời hạn cho vay của từng khoản cấp tín dụng cụ thể được tính từ ngày tiếp theo của ngày giải ngân cho đến thời điểm trả hết toàn bộ tiền gốc, lãi tiền vay và các chi phí phát sinh liên quan. Trong trường hợp Bên B sử dụng tiền vay chưa đủ một ngày, thì tính từ thời điểm nhận tiền vay và thời gian vay vốn được tính là 01 (một) ngày và trường hợp ngày cuối cùng của thời hạn vay là ngày lễ hoặc thứ 7, chủ nhật hàng tuần, thì ngày đến hạn chuyển sang ngày làm việc tiếp theo.");
            safePutReplacement("{{tghm}}", "Thời gian xác định bình quân cho một chu kỳ sản xuất, kinh doanh.");
            safePutReplacement("{{vqhm}}", "Vòng quay vốn lưu động = Tổng số ngày 01 năm/Tổng số ngày bình quân = 365/304 = 1,2 vòng.");
            safePutReplacement("{{vongQuay}}", "- Số vòng quay vốn bình quân:  1,2  Vòng/năm.");
            safePutReplacement("{{hm1}}", "+ Chính sách bán hàng: Bán buôn và bán lẻ cho các hộ kinh doanh, các đại lý trên địa bàn tỉnh và các vùng lân cận.");
            safePutReplacement("{{hm2}}", "+ Chính sách thu tiền hàng: Cho phép bên mua trả chậm tối đa không quá 90 ngày.");
            safePutReplacement("{{hm3}}", "* Xác định thời gian bình quân cho một chu kỳ sản xuất:");
            safePutReplacement("{{tgpa}}", "duy trì hàn mức");
            safePutReplacement("{{hm4}}", "cao nhất / hạn mức");
            safePutReplacement("{{hm5}}", "hạn mức");
            safePutReplacement("{{hm6}}", "Thời hạn mỗi giấy nhận nợ tối đa: 10 tháng.");
            safePutReplacement("{{dieu31}}", "Mỗi lần đề nghị giải ngân vốn vay: Bên B lập báo cáo đề xuất giải ngân kiêm giấy nhận nợ, cung cấp đầy đủ cho Bên A hồ sơ, tài liệu chứng minh mục đích sử dụng tiền vay phù hợp với mục đích nêu tại khoản 3, Điều 1 của Hợp đồng này.");
            safePutReplacement("{{dieu32}}", "Bằng tiền mặt hoặc chuyển khoản.");
            safePutReplacement("{{dieu33}}", "");
            safePutReplacement("{{dieu34}}", "");
            safePutReplacement("{{dieu35}}", "");
            safePutReplacement("{{dieu36}}", "");
            safePutReplacement("{{dieu37}}", "");
            safePutReplacement("{{dieu5}}", "- Bên B được trả nợ gốc, lãi tiền vay nhiều lần trước hạn hoặc theo đúng định kỳ trả nợ của từng lần giải ngân theo thời hạn trả nợ vay được ghi trên từng Giấy nhận nợ tương ứng. Bên A được quyền ưu tiên thu nợ các khoản vay có Giấy nhận nợ đã quá hạn, đã cơ cấu lại thời hạn trả nợ và Giấy nhận nợ có ngày đáo hạn đến trước. Số tiền trả nợ gốc tiền vay được căn cứ vào dư nợ thực tế tại thời điểm đến hạn.");
            safePutReplacement("{{dieu5s}}", request.getPavvRequest().getNgayThuLaiHM() + " hàng tháng");
            safePutReplacement("{{dieu2}}", "Lãi suất tiền vay trong hạn: Tại thời điểm hai bên giao thỏa thuận mức lãi suất tiền vay được xác định tại thời điểm bên B nhận nợ tiền vay và phù hợp với Nghị quyết hoặc Quyết định về lãi suất cho vay của Hội đồng quản trị.");

        } else {
            safePutReplacement("{{lvt}}", Optional.of("Ngắn hạn").orElse(""));
            safePutReplacement("{{slv}}", "Từng lần");
            safePutReplacement("{{ms1t}}", "Số tiền cho vay:");
            safePutReplacement("{{ms1d}}", "Theo các điều khoản và điều kiện của Hợp đồng tín dụng này, bên A cho bên B vay khoản tiền bằng đồng Việt Nam. Số tiền vay là: " + request.getTienSo() + " đồng, (Bằng chữ: " + request.getTienChu() + " ).");
            safePutReplacement("{{ms2t}}", "Thời hạn cho vay: ");
            safePutReplacement("{{ms2d}}", "Thời hạn cho vay là: " + request.getHanMuc() + ", được tính từ ngày tiếp theo của ngày giải ngân đến ngày " + ngayKetThuc + " (trường hợp ngày cuối cùng của thời hạn vay là ngày lễ hoặc là ngày thứ 7, chủ nhật hàng tuần, thì ngày đến hạn chuyển sang ngày làm việc tiếp theo; nếu trường hợp bên B sử dụng chưa đủ một ngày, thì tính từ thời điểm nhận tiền vay và thời gian vay vốn được tính là 01 (một) ngày)");
            safePutReplacement("{{ms2e}}", "");
            safePutReplacement("{{ms2f}}", "");
            safePutReplacement("{{ms3}}", "Phương thức cho vay: Cho vay Từng lần.");
            safePutReplacement("{{ms4}}", "Mục đích sử dụng vốn vay: " + request.getMuchDichVay());
            safePutReplacement("{{ms5}}", "");
            safePutReplacement("{{ms6t}}", "");
            safePutReplacement("{{ms6d}}", "");
            safePutReplacement("{{tghm}}", "");
            safePutReplacement("{{vqhm}}", "");
            safePutReplacement("{{vongQuay}}", "");
            safePutReplacement("{{hm1}}", "");
            safePutReplacement("{{hm2}}", "");
            safePutReplacement("{{hm3}}", "");
            safePutReplacement("{{hm4}}", "");
            safePutReplacement("{{hm5}}", "");
            safePutReplacement("{{hm6}}", "");
            safePutReplacement("{{tgpa}}", "sử dụng vốn vay");
            safePutReplacement("{{soHDTDcu}}", "");
            safePutReplacement("{{ngayHDTDcu}}", "");
            safePutReplacement("{{thvGNN}}", "Thời hạn vay " + request.getThoiHanVay() + " năm.");
            safePutReplacement("{{tienGNN}}", "Tổng số tiền vay:  " + request.getTienSo() + " đồng.");
            safePutReplacement("{{vongQuayGNN}}", "");
            safePutReplacement("{{dieu5}}", "- Bên B được trả nợ gốc, lãi tiền vay nhiều lần trước hạn hoặc theo đúng định kỳ trả nợ và hạn trả nợ cuối cùng là ngày " + ngayKetThuc + ". Trường hợp ngày đến hạn trùng với ngày nghỉ, lễ, tết hoặc thứ 7, chủ nhật hàng tuần thì ngày đến hạn được chuyển sang ngày làm việc tiếp theo.");
            safePutReplacement("{{dieu5s}}", "kế tiếp ngày giải ngân trên hợp đồng này của mỗi tháng trong suốt thời hạn vay");
            safePutReplacement("{{dieu31}}", "Bên A cân đối được nguồn vốn vay.");
            safePutReplacement("{{dieu32}}", "Khi bên A thực hiện giải ngân vốn vay bên B phải cung cấp đầy đủ hồ sơ đề nghị giải ngân vốn vay có nội dung, hình thức, số lượng đáp ứng theo yêu cầu của bên A, bao gồm:");
            safePutReplacement("{{dieu33}}", "a) Giấy nhận nợ.");
            safePutReplacement("{{dieu34}}", "b) Tài liệu chứng minh mục đích sử dụng vốn vay.");
            safePutReplacement("{{dieu35}}", "c) Thông tin của người có liên quan đang vay vốn tại Quỹ tín dụng Thái Học và các tài liệu bổ sung khác (nếu có) theo yêu cầu của bên A.");
            safePutReplacement("{{dieu36}}", "3. Hình thức cấp tiền vay: Một lần.");
            safePutReplacement("{{dieu37}}", "4. Phương thức giải ngân vốn vay: Chuyển khoản.");
            safePutReplacement("{{dieu2}}", "Lãi suất tiền vay thỏa thuận: Tại thời điểm bên B ký kết Hợp đồng tín dụng và nhận nợ tiền vay là được sự thỏa thuận giữa các Bên và được xác định cụ thể theo Nghị quyết hoặc Quyết định về lãi suất cho vay của Hội đồng quản trị. ");
        }
        CreditContractPAVVRequest pavvDto = request.getPavvRequest();
        if (pavvDto != null && Boolean.TRUE.equals(pavvDto.getCheckAddress())) {
            String address = "- Địa điểm thực hiện phương án: " + pavvDto.getAddress();
            safePutReplacement("{{ddpavv}}", address);
        } else {
            safePutReplacement("{{ddpavv}}", "");
        }

        if (pavvDto != null) {
            safePutReplacement("{{tenpavv}}", Optional.ofNullable(pavvDto.getName()).orElse(""));
            safePutReplacement("{{ldpavv}}", Optional.ofNullable(pavvDto.getReason()).orElse(""));
            if (pavvDto.getLoaiPhuongAn().equals("chanNuoi")) {
                safePutReplacement("{{loaiPhuongAn}}", "chăn nuôi");
                safePutReplacement("{{sanPhamSX}}", "Sản phẩm của khách hàng là nguồn lương thực thực phẩm cẩn thiết được chế biến và cung cấp trên thị trường, phù hợp với nhu cầu của người tiêu dùng. Việc mở rộng quy mô chăn nuôi mang lại kinh tế cho khách hàng, nâng cao năng suất, hiệu quả và sức cạnh tranh trong cơ chế thị trường.");
                safePutReplacement("{{thiTruongDV}}", "Nguồn cung ứng nguyên vật liệu, thiết bị và dịch vụ phục vụ sản xuất – kinh doanh hiện nay rất đa dạng, phong phú và có chất lượng ổn định. Doanh nghiệp có nhiều lựa chọn từ các nhà cung cấp uy tín, đảm bảo tiêu chuẩn kỹ thuật và giá cả cạnh tranh. Hệ thống giao thông thuận tiện giúp việc vận chuyển nguyên liệu, hàng hóa nhanh chóng, tiết kiệm thời gian và chi phí. Bên cạnh đó, nhiều chương trình hỗ trợ từ phía nhà sản xuất, nhà phân phối và chính quyền địa phương giúp doanh nghiệp giảm thiểu chi phí đầu vào, tạo điều kiện thuận lợi để tối ưu hóa hiệu quả sản xuất – kinh doanh.");
                safePutReplacement("{{pavv1}}", "- Vị trí xây dựng ao chuồng phù hợp về phát triển chăn nuôi ở địa phương.");
                safePutReplacement("{{pavv2}}", "- Có nguồn nước đảm bảo chất lượng, khu tập kết và xử lý chất thải.");
                safePutReplacement("{{pavv3}}", "- Các biện pháp về bảo vệ môi trường được đảm bảo theo quy định.");
                safePutReplacement("{{pavv4}}", "- Có diện tích ao chuồng khoảng 1000 m², mang thiết bị đảm bảo cho việc chăn nuôi.");
                safePutReplacement("{{pavv5}}", "- Có nhật ký ghi chép trong quá trình hoạt động chăn nuôi.");
                safePutReplacement("{{pavv6}}", "- Có khoảng cách an toàn từ khu vực chăn nuôi đến các Tổ Dân Phố.");
            } else {
                safePutReplacement("{{loaiPhuongAn}}", "sản xuất, kinh doanh");
                safePutReplacement("{{sanPhamSX}}", "Sản phẩm dịch vụ sản xuất, kinh doanh của khách hàng đang có nhu cầu lớn trên thị trường, đồng thời phù hợp với nhu cầu của người sử dụng. Việc mở rộng quy mô sản xuất và nâng cao chất lượng sản phẩm, thu hút được khách hàng và sản phẩm có tính cạnh tranh cao hơn trên thị trường.");
                safePutReplacement("{{thiTruongDV}}", "Có nhiều nhà cung cấp chất lượng để lựa chọn, giao thông thuận tiện để vận chuyển nguồn thức ăn. Nhà sản xuất có nhiều chương trình hỗ trợ người chăn nuôi nên giá đầu vào thấp hơn, giảm thiểu chi phí đầu vào tối đa.");
                safePutReplacement("{{pavv1}}", "- Khả năng thực hiện phương án là khả thi.");
                safePutReplacement("{{pavv2}}", "- Điều kiện về môi trường được đảm bảo, thực hiện đúng quy định địa phương về vệ sinh môi trường, không để ảnh hưởng đến hộ dân xung quanh, nguồn nước. Tuân thủ phòng chống cháy nổ theo quy định của pháp luật.");
                safePutReplacement("{{pavv3}}", "");
                safePutReplacement("{{pavv4}}", "");
                safePutReplacement("{{pavv5}}", "");
                safePutReplacement("{{pavv6}}", "");
            }
        }

        safePutReplacement("{{thv}}", Optional.ofNullable(request.getThoiHanVay()).orElse(""));
//        safePutReplacement("{{ncd}}", Optional.ofNullable(request.getNhaCoDinh()).orElse(""));
//        safePutReplacement("{{tsbds}}", Optional.ofNullable(request.getTongTaiSanBD()).orElse(""));
//        safePutReplacement("{{tsbdc}}", Optional.ofNullable(request.getTongTaiSanBDChu()).orElse(""));
        safePutReplacement("{{day}}", String.format("%02d", date.getDayOfMonth()));
        safePutReplacement("{{month}}", String.format("%02d", date.getMonthValue()));
        safePutReplacement("{{year}}", String.valueOf(date.getYear()));
        if (request.getTaiSanArray() != null && !request.getTaiSanArray().isEmpty()) {
            List<String> dsDoanVan = new ArrayList<>();

            for (CreditContractTSBDRequest tstc : request.getTaiSanArray()) {
                if (tstc.getSoHopDongTheChapQSDD() != null && tstc.getNgayTheChap() != null) {
                    String doan = "Hợp đồng thế chấp quyền sử dụng đất số: "
                            + tstc.getSoHopDongTheChapQSDD()
                            + "/HĐTC được các bên thỏa thuận, nhất trí xác lập, giao kết vào ngày: "
                            + tstc.getNgayTheChap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    dsDoanVan.add(doan);
                }
            }
            // Ghép lại: nếu nhiều tài sản thì nối bằng " và "
            String ketQua = String.join(" và ", dsDoanVan);
            safePutReplacement("{{dsHopDongTheChap}}", ketQua);
        }
        safePutReplacement("{{canBoTD}}", "NGUYỄN ĐỒNG CHÍNH");
        safePutReplacement("{{sdtCanBoTD}}", "0343304666");
        safePutReplacement("{{canBoTDVT}}", capitalizeWords("NGUYỄN ĐỒNG CHÍNH"));
        String regex = "\\d+(,\\d+)?";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(request.getLaiSuat());
        if (matcher.find()) {
            String result = matcher.group();
            safePutReplacement("{{lss}}", result);
            System.out.println("Kết quả: " + result);
        } else {
            System.out.println("Không tìm thấy số.");
        }
        // 👉 Placeholder cho nội dung vay lại
        System.err.println("pavvREQUEST " + request.getPavvRequest());
        long duNoTruoc = 0;
        long duNoSau = 0;
        long soTienVayLaiLanNay = 0;
        CreditContractPAVVRequest pavvDtoVayLai = request.getPavvRequest();
        if (pavvDto != null && pavvDtoVayLai.getReLoanSequence() != null && pavvDtoVayLai.getReLoanSequence() != 0) {
            System.err.println("so hop dong vay lai: " + pavvDtoVayLai.getReLoanSequence());
            // Nội dung vay lại, bạn có thể tùy chỉnh câu văn
//            safePutReplacement("{{noiDung}}", "Biên bản xác định lại giá trị tài sản bổ sung cho HĐTC số: " + request.getSoHopDongTheChapQSDD()
//                    + " Ngày " + String.format("%02d", dateTC.getDayOfMonth()) + " tháng " + String.format("%02d", dateTC.getMonthValue()) + " năm " + String.valueOf(dateTC.getYear()));
            // Số hợp đồng vay lại
            safePutReplacement("{{shdvl}}", "" + pavvDtoVayLai.getReLoanSequence());
            safePutReplacement("{{shdvlts}}", "." + pavvDtoVayLai.getReLoanSequence());
            safePutReplacement("{{xdlai}}", "LẠI");
        } else {
//            safePutReplacement("{{noiDung}}", "");
            safePutReplacement("{{shdvl}}", "");
            safePutReplacement("{{shdvlts}}", "");
            safePutReplacement("{{xdlai}}", "");
        }
        if (Boolean.TRUE.equals(request.getPavvRequest().getGiaiNganHM())) {
            duNoTruoc = parseLongSafe(pavvDtoVayLai.getDuNoTruoc());
            soTienVayLaiLanNay = parseLongSafe(pavvDtoVayLai.getSoTienVayLanNay());
//            if(soTienVayLaiLanNay)
            System.err.println("soTienVayLaiLanNay --> " + soTienVayLaiLanNay);
            if (soTienVayLaiLanNay == 0) {
                soTienVayLaiLanNay = tienSo;
            }
        } else {
            soTienVayLaiLanNay = tienSo;
        }
        duNoSau = duNoTruoc + soTienVayLaiLanNay;

        System.err.println("tien vay lai --> " + formatCurrency(soTienVayLaiLanNay));
        safePutReplacement("{{duNoTruoc}}", formatCurrency(duNoTruoc));
        safePutReplacement("{{duNoTruocChu}}", numberToVietnameseWordsMoney(duNoTruoc));
        safePutReplacement("{{tienVayLai}}", formatCurrency(soTienVayLaiLanNay));
        safePutReplacement("{{tienVayLaiChu}}", numberToVietnameseWordsMoney(soTienVayLaiLanNay));
        safePutReplacement("{{duNoSau}}", formatCurrency(duNoSau));
        safePutReplacement("{{duNoSauChu}}", numberToVietnameseWordsMoney(duNoSau));
        if (request.getNguoiDaiDien().equalsIgnoreCase("gd")) {
            safePutReplacement("{{dcpgd1}}", "- Trụ sở tại: Số 178, TDP Ninh Chấp 5, phường Chu Văn An, thành phố Hải Phòng.");
            safePutReplacement("{{dcpgd2}}", "- Giấy phép đăng ký kinh doanh: 0800001806; Điện thoại: 02203.882.700");
            safePutReplacement("{{ndd}}", "bà: PHÙNG THỊ LOAN Chức vụ: Giám Đốc điều hành");
            safePutReplacement("{{ndd1}}", "Bà: " + capitalizeWords("PHÙNG THỊ LOAN") + " - Chức vụ: Giám Đốc điều hành.");
            safePutReplacement("{{pgd}}", "");
            safePutReplacement("{{pgdvt}}", "");
            safePutReplacement("{{phuong}}", "Chu Văn An");
            safePutReplacement("{{chuTichPhuong}}", "..........................");
            safePutReplacement("{{nguoiTiepNhanHoSo}}", "Phạm Thị Thơm");
            safePutReplacement("{{diaChiLienHe}}", "Số 178 Ninh Chấp 5, phường Chu Văn An,");
            safePutReplacement("{{gmail}}", "thaihocqtd@gmail.com");
            safePutReplacement("{{nddpl}}", "Giám Đốc");
            safePutReplacement("{{gdpgd}}", "Phùng Thị Loan");
            safePutReplacement("{{nddPGD1}}", "CCCD số: 030182016564; Cấp ngày: 22/12/2021.");
            safePutReplacement("{{nddPGD2}}", "Nơi cấp: Cục cảnh sát quản lý hành chính về trật tự xã hội.");
        } else if (request.getNguoiDaiDien().equalsIgnoreCase("pgd")) {
            safePutReplacement("{{pgd}}", " - PGD AN LẠC");
            safePutReplacement("{{pgdvt}}", capitalizeWords(" - PHÒNG GIAO DỊCH AN LẠC"));
            safePutReplacement("{{dcpgd1}}", "Địa chỉ: TDP Lạc Đạo, phường Lê Đại Hành, thành phố Hải Phòng.");
            safePutReplacement("{{dcpgd2}}", "Giấy phép đăng ký kinh doanh: 0800001806; Điện thoại: 0220.3596.266");
            safePutReplacement("{{ndd}}", "ông: DƯƠNG QUANG TUẤN Chức vụ: Giám Đốc PGD An Lạc.");
            safePutReplacement("{{nddPGD1}}", "CCCD số: 030087002460; (Theo văn bản ủy quyền số: 02/UQ-TN Ngày 15 tháng ");
            safePutReplacement("{{nddPGD2}}", "07 năm 2026 của Giám Đốc Quỹ Tín Dụng Nhân Dân Thái Học).");
            safePutReplacement("{{ndd1}}", "Ông: " + capitalizeWords("DƯƠNG QUANG TUẤN") + " - Chức vụ: Giám Đốc PGD An Lạc.");
            safePutReplacement("{{phuong}}", "Lê Đại Hành");
            safePutReplacement("{{chuTichPhuong}}", "Phương Quốc Luyện");
            safePutReplacement("{{nguoiTiepNhanHoSo}}", "Nguyễn Văn Chiến");
            safePutReplacement("{{diaChiLienHe}}", "TDP Lạc Đạo, phường Lê Đại Hành,");
            safePutReplacement("{{gmail}}", "pgdanlac888@gmail.com");
            safePutReplacement("{{nddpl}}", "Giám Đốc PGD");
            safePutReplacement("{{gdpgd}}", "Dương Quang Tuấn");
        }
        if (request.getTaiSanArray() != null && !request.getTaiSanArray().isEmpty()) {
            StringBuilder doanVanBan = new StringBuilder();
            StringBuilder doanVanBan2 = new StringBuilder();

            for (CreditContractTSBDRequest tstc : request.getTaiSanArray()) {
                if (Boolean.TRUE.equals(tstc.getCheckHopDongBaoLanh())) {
                    // Nếu tài sản này có checkHopDongBaoLanh
                    doanVanBan.append("Bên B dùng tài sản này để đảm bảo việc thanh toán được kịp thời, đầy đủ và thực hiện một cách trọn vẹn ")
                            .append("khi đến hạn các nghĩa vụ trả nợ đối với hợp đồng cho vay số: ")
                            .append(request.getSoHopDongTD())
                            .append(" của ")
                            .append(request.getGtkh().toLowerCase()).append(" ")
                            .append(capitalizeWords(request.getTenKhachHang())).append(" ")
                            .append(request.getGtnt().toLowerCase()).append(" ")
                            .append(capitalizeWords(request.getTenNguoiThan()))
                            .append(" hoặc các hợp đồng cho vay khác có tham chiếu từ hợp đồng thế chấp này.\n");

                    doanVanBan2.append("Theo hợp đồng cho vay số: ")
                            .append(request.getSoHopDongTD())
                            .append(" và hợp đồng cho vay khác (nếu có) mà tài sản thế chấp này làm bảo đảm.\n");
                } else {
                    // Nếu tài sản này không checkHopDongBaoLanh
                    doanVanBan.append("Để đảm bảo việc thanh toán được kịp thời, đầy đủ và thực hiện một cách trọn vẹn khi đến hạn các nghĩa vụ trả nợ đang ")
                            .append("tồn tại hoặc sẽ phát sinh trong tương lai của Bên B cho Bên A theo các Hợp đồng cho vay và/hoặc các Hợp đồng khác có tham chiếu từ Hợp đồng này.\n");

                    doanVanBan2.append("của Bên B\n");
                }
            }

            safePutReplacement("{{tstc}}", doanVanBan.toString());
            safePutReplacement("{{dvb}}", doanVanBan2.toString());
        }

        // Bước 1: tìm tất cả paragraph chứa placeholder
        List<XWPFParagraph> targets = new ArrayList<>();
        for (XWPFParagraph para : doc.getParagraphs()) {
            String text = para.getText();
            if (text == null) continue;

            if (text.contains("{{TABLE_PLACEHOLDER}}")
                    || text.contains("{{TABLE1_PLACEHOLDER}}")
                    || text.contains("{{TABLE2_PLACEHOLDER}}")
                    || text.contains("{{TABLE3_PLACEHOLDER}}")
                    || text.contains("{{TABLE_HM_PLACEHOLDER}}")
                    || text.contains("{{TABLE_CP_PLACEHOLDER}}")
                    || text.contains("{{TABLE_TN_PLACEHOLDER}}")
                    || text.contains("{{TABLE_PLHM_PLACEHOLDER}}")
            ) {
                targets.add(para);
            }
        }
// Bước 2: thay thế placeholder bằng bảng
        for (XWPFParagraph para : targets) {
            String text = para.getText();

            if (text.contains("{{TABLE_PLACEHOLDER}}")) {
                if (request.getTableRequest() != null && request.getTableRequest().isDrawTable()) {
                    insertTableAtPlaceholder(doc, para, request.getTableRequest(), true, replacements, request);
                }
                // xóa placeholder sau cùng
                int paraPos = doc.getPosOfParagraph(para);
                if (paraPos >= 0) {
                    doc.removeBodyElement(paraPos);
                }
            }

            if (text.contains("{{TABLE_TN_PLACEHOLDER}}")) {
                insertTableAtPlaceholder(doc, para, request.getThuNhapDuKienTable(), false, replacements, request);
                int paraPos = doc.getPosOfParagraph(para);
                if (paraPos >= 0) doc.removeBodyElement(paraPos);
            }

            if (text.contains("{{TABLE1_PLACEHOLDER}}") && ts != null && ts.getTable1() != null) {
                insertTableAtPlaceholder(doc, para, ts.getTable1(), false, replacements, request);
                int paraPos = doc.getPosOfParagraph(para);
                if (paraPos >= 0) doc.removeBodyElement(paraPos);
            }

            if (text.contains("{{TABLE2_PLACEHOLDER}}") && ts != null && ts.getTable2() != null) {
                insertTableAtPlaceholder(doc, para, ts.getTable2(), false, replacements, request);
                int paraPos = doc.getPosOfParagraph(para);
                if (paraPos >= 0) doc.removeBodyElement(paraPos);
            }

            if (text.contains("{{TABLE3_PLACEHOLDER}}")) {
                if (request.getTaiSanArray() != null && !request.getTaiSanArray().isEmpty()) {
                    for (CreditContractTSBDRequest tsbdDto : request.getTaiSanArray()) {
                        if (tsbdDto.getTable3() != null) {
                            insertTableAtPlaceholder(doc, para, tsbdDto.getTable3(), false, replacements, request);
                        }
                    }
                } else if (ts != null && ts.getTable3() != null) {
                    insertTableAtPlaceholder(doc, para, ts.getTable3(), false, replacements, request);
                }
                // xóa placeholder sau khi đã chèn xong tất cả bảng
                int paraPos = doc.getPosOfParagraph(para);
                if (paraPos >= 0) doc.removeBodyElement(paraPos);
            }

            if (text.contains("{{TABLE_PLHM_PLACEHOLDER}}")) {
                insertTableAtPlaceholder(doc, para, request.getPhuLucHanMucTable(), false, replacements, request);
                int paraPos = doc.getPosOfParagraph(para);
                if (paraPos >= 0) doc.removeBodyElement(paraPos);
            }

            if (text.contains("{{TABLE_HM_PLACEHOLDER}}")) {
                if (request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)")) {
                    insertTableAtPlaceholder(doc, para, request.getHanMucTable(), false, replacements, request);
                }
                // luôn xóa placeholder để tránh header rỗng
                int paraPos = doc.getPosOfParagraph(para);
                if (paraPos >= 0) doc.removeBodyElement(paraPos);
            }

            if (text.contains("{{TABLE_CP_PLACEHOLDER}}")) {
                insertTableAtPlaceholder(doc, para, request.getChiPhiTable(), false, replacements, request);
                int paraPos = doc.getPosOfParagraph(para);
                if (paraPos >= 0) doc.removeBodyElement(paraPos);
            }
        }

        String vonLuuDongStr = replacements.get("{{vonLuuDong}}");
        if (vonLuuDongStr != null && !vonLuuDongStr.isEmpty()) {
            // Loại bỏ dấu chấm phân cách hàng nghìn
            String cleanedStr = vonLuuDongStr.replace(".", "");

            long vonLuuDong = Long.parseLong(cleanedStr);
            safePutReplacement("{{vld}}", "- Vốn lưu động cần thiết cho một vòng quay: " + vonLuuDongStr + " đồng");

            long khoanThuBQ = vonLuuDong - soTienVayLaiLanNay - duNoTruoc;
            safePutReplacement("{{khoanThuBQ}}", formatCurrency(khoanThuBQ));

            long soDuConLai = tienSo - duNoTruoc;
            safePutReplacement("{{soDuConLai}}", formatCurrency(soDuConLai));
        } else {
            safePutReplacement("{{vld}}", "");
        }
        for (XWPFParagraph p : doc.getParagraphs()) {
            for (XWPFRun r : p.getRuns()) {
                String text = r.getText(0);
                if (text != null && text.contains("{{taiSanBlock}}")) {
                    r.setText("", 0); // xóa placeholder

                    List<CreditContractTSBDRequest> tsArray = request.getTaiSanArray();
                    List<String> allLines = new ArrayList<>();

                    for (int t = 0; t < tsArray.size(); t++) {
                        String block = buildTaiSanBlock(tsArray.get(t));
                        allLines.addAll(Arrays.asList(block.split("\n", -1)));

                        // nối bằng "Và" giữa các tài sản (trừ tài sản cuối)
                        if (t < tsArray.size() - 1) {
                            allLines.add("Và");
                        }
                    }

                    for (int i = 0; i < allLines.size(); i++) {
                        if (i == 0) {
                            r.setText(allLines.get(i), 0);
                        } else {
                            r.addBreak(); // đổi từ addCarriageReturn() sang addBreak()
                            r.setText(allLines.get(i));
                        }
                    }
                }
            }
        }
        for (XWPFParagraph p : doc.getParagraphs()) {
            for (XWPFRun r : p.getRuns()) {
                String text = r.getText(0);
                if (text != null && text.contains("{{taiSanBlockThongBao}}")) {
                    r.setText("", 0); // xóa placeholder

                    List<CreditContractTSBDRequest> tsArray = request.getTaiSanArray();
                    List<String> allLines = new ArrayList<>();

                    for (int t = 0; t < tsArray.size(); t++) {
                        String block = buildTaiSanBlockThongBao(tsArray.get(t));
                        allLines.addAll(Arrays.asList(block.split("\n", -1)));

                        // nối bằng "Và" giữa các tài sản (trừ tài sản cuối)
                        if (t < tsArray.size() - 1) {
                            allLines.add("Và");
                        }
                    }

                    for (int i = 0; i < allLines.size(); i++) {
                        if (i == 0) {
                            r.setText(allLines.get(i), 0);
                        } else {
                            r.addBreak(); // đổi từ addCarriageReturn() sang addBreak()
                            r.setText(allLines.get(i));
                        }
                    }
                }
            }
        }


//        System.err.println("TIEN SO ====> " + tienSo);
//        long tsbds = parseLongSafe(ts.getTongTaiSanBD());
//        System.err.println("TSBD ==>" + tsbds);
//// Tính tỷ lệ và làm tròn 1 chữ số thập phân
//        double phanTramTyLe = 0.0;
//        if (tsbds != 0) {
//            phanTramTyLe = Math.round(((double) tienSo / tsbds) * 1000.0) / 10.0;
//            // (tienSo/tsbds)*100 rồi làm tròn 1 chữ số thập phân
//        }
//
//        safePutReplacement("{{phanTramTyLe}}", String.valueOf(phanTramTyLe));
//        calculateTyLeChoVay(replacements, request, ts);
        long loiNhuan = parseLongSafe(replacements.get("{{loiNhuan}}"));
        long thuHoiVon = tienSo - loiNhuan;
        safePutReplacement("{{thuHoiVon}}", formatCurrency(thuHoiVon));

// Bước 3: xử lý các paragraph text khác
        List<XWPFParagraph> docParas = new ArrayList<>(doc.getParagraphs());
        for (XWPFParagraph paragraph : docParas) {
            processParagraph(paragraph, replacements);
        }

// Bước 4: xử lý các paragraph trong bảng
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    List<XWPFParagraph> paras = new ArrayList<>(cell.getParagraphs());
                    for (XWPFParagraph paragraph : paras) {
                        processParagraph(paragraph, replacements);
                    }
                }
            }
        }

    }

    private String buildTaiSanBlock(CreditContractTSBDRequest ts) {
        StringBuilder sb = new StringBuilder();

        sb.append(Optional.ofNullable(ts.getNoiDungNgoaiBia()).orElse(""))
                .append("; Số phát hành: ")
                .append(Optional.ofNullable(ts.getSerial()).orElse(""))
                .append("; ")
                .append(Optional.ofNullable(ts.getNoiDungVaoSo()).orElse(""))
                .append("\n");

        sb.append("Ngày cấp: ").append(Optional.ofNullable(ts.getNgayCapSo()).orElse(""))
                .append("; Nơi cấp: ").append(Optional.ofNullable(ts.getNoiCapSo()).orElse(""))
                .append("\n");

        sb.append("- Tên chủ sử dụng đất: ").append(resolveNguoiMangTen(ts)).append("\n");

        sb.append("- Vị trí thửa đất tại: ")
                .append(Optional.ofNullable(ts.getDiaChiThuaDat()).orElse(""))
                .append("\n");

        sb.append("- Thửa đất số: ").append(Optional.ofNullable(ts.getSoThuaDat()).orElse(""))
                .append(" - Tờ bản đồ: ").append(Optional.ofNullable(ts.getSoBanDo()).orElse(""))
                .append("\n");

        sb.append("- Diện tích đất thế chấp: ")
                .append(Optional.ofNullable(ts.getDienTichDatSo()).orElse(""))
                .append(" m² (Bằng chữ: ")
                .append(Optional.ofNullable(ts.getDienTichDatChu()).orElse(""))
                .append(" mét vuông)\n");
        // 1. Loại đất
        appendIfChecked(sb, ts.getCheckLoaiDat(), ts.getLoaiDat(), "- Loại đất: ");

        // Hình thức sử dụng - không có checkbox riêng, giữ nguyên logic cũ
        if (StringUtils.isNotBlank(ts.getHinhThucSuDung())) {
            sb.append("- Hình thức sử dụng: ").append(ts.getHinhThucSuDung()).append("\n");
        }

        // 2. Mục đích sử dụng
        appendIfChecked(sb, ts.getCheckMucDich(), ts.getMuchDichSuDung(), "- Mục đích sử dụng: ");

        // 3. Nguồn gốc sử dụng
        appendIfChecked(sb, ts.getCheckNguonGocSuDung(), ts.getNguonGocSuDung(), "- Nguồn gốc sử dụng: ");
        // 4. Ghi chú
        appendIfChecked(sb, ts.getCheckGhiChu(), ts.getGhiChu(), "- Ghi chú: ");
        sb.append("- Thời hạn sử dụng đất: ")
                .append(Optional.ofNullable(ts.getThoiHanSuDung()).orElse(""))
                .append("\n");
        sb.append("- Giá trị quyền sử dụng đất: ").append(Optional.ofNullable(ts.getTongTaiSanBD()).orElse(""))
                .append(" đồng").append(" (Bằng chữ: ").append(numberToVietnameseWordsMoney(parseLongSafe(ts.getTongTaiSanBD()))).append(")");
        return sb.toString();
    }

    private String buildTaiSanBlockThongBao(CreditContractTSBDRequest ts) {
        StringBuilder sb = new StringBuilder();
        sb.append("- Quyền sử dụng đất trên số thửa: ").append(Optional.ofNullable(ts.getSoThuaDat()).orElse(""))
                .append("; Tờ bản đồ: ").append(Optional.ofNullable(ts.getSoBanDo()).orElse(""))
                .append("\n");

        sb.append("- Diện tích: ")
                .append(Optional.ofNullable(ts.getDienTichDatSo()).orElse(""))
                .append(" m² (Bằng chữ: ")
                .append(Optional.ofNullable(ts.getDienTichDatChu()).orElse(""))
                .append(" mét vuông)\n");
        sb.append("- Theo giấy chứng nhận quyền sử dụng đất số: ")
                .append(Optional.ofNullable(ts.getSerial()).orElse(""))
                .append("\n");
        sb.append("- Do: ")
                .append(Optional.ofNullable(ts.getNoiCapSo()).orElse(""))
                .append("\n");
        sb.append("- Thuộc quyền sở hữu của Ông (Bà): ")
                .append(resolveNguoiSoHuu(ts))
                .append("\n");
        sb.append("- Địa chỉ thửa đất:  ")
                .append(Optional.ofNullable(ts.getDiaChiThuaDat()).orElse(""))
                .append("\n");
        // 1. Loại đất
        appendIfChecked(sb, ts.getCheckLoaiDat(), ts.getLoaiDat(), "- Loại đất: ");
        // Hình thức sử dụng - không có checkbox riêng, giữ nguyên logic cũ
        if (StringUtils.isNotBlank(ts.getHinhThucSuDung())) {
            sb.append("- Hình thức sử dụng: ").append(ts.getHinhThucSuDung()).append("\n");
        }
        // 2. Mục đích sử dụng
        appendIfChecked(sb, ts.getCheckMucDich(), ts.getMuchDichSuDung(), "- Mục đích sử dụng: ");
        // 3. Nguồn gốc sử dụng
        appendIfChecked(sb, ts.getCheckNguonGocSuDung(), ts.getNguonGocSuDung(), "- Nguồn gốc sử dụng: ");
        // 4. Ghi chú
        appendIfChecked(sb, ts.getCheckGhiChu(), ts.getGhiChu(), "- Ghi chú: ");
        sb.append("- Thời hạn sử dụng đất: ")
                .append(Optional.ofNullable(ts.getThoiHanSuDung()).orElse(""));
        return sb.toString();
    }

    private void appendIfChecked(StringBuilder sb, Boolean checked, String value, String prefix) {
        if (Boolean.TRUE.equals(checked) && StringUtils.isNotBlank(value)) {
            sb.append(prefix).append(value).append("\n");
        }
    }

    private String resolveNguoiMangTen(CreditContractTSBDRequest ts) {
        if (ts.getCheckNguoiMangTenBiaDo()) {
            return Optional.ofNullable(ts.getNguoiMangTen()).orElse("");
        } else {
            String nguoiMangTen = capitalizeWords(ts.getDungTenBiaDo1());

            if (ts.getCheckChiMangTenNguoi2() && !ts.getCheckChiMangTenNguoi1()) {
                // chỉ người 2
                nguoiMangTen = capitalizeWords(ts.getDungTenBiaDo2());
            } else if (ts.getCheckChiMangTenNguoi2() && ts.getCheckChiMangTenNguoi1()) {
                // cả người 1 và người 2
                nguoiMangTen = capitalizeWords(ts.getDungTenBiaDo1()) + " và " + capitalizeWords(ts.getDungTenBiaDo2());
            }
            return nguoiMangTen;
        }
    }

    private String resolveNguoiSoHuu(CreditContractTSBDRequest ts) {
        if (ts.getCheckNguoiMangTenBiaDo()) {
            return Optional.ofNullable(ts.getNguoiMangTen()).orElse("");
        } else {
            String nguoiSoHuu = capitalizeWords(ts.getDungTenBiaDo1());
            if (Boolean.TRUE.equals(ts.getCheckDongSoHuu())) {
                // cả người 1 và người 2
                nguoiSoHuu = capitalizeWords(ts.getDungTenBiaDo1()) + " và " + capitalizeWords(ts.getDungTenBiaDo2());
            }
            return nguoiSoHuu;
        }
    }

    public static String formatCurrency(Long value) {
        if (value == null) return "";
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(value);
    }

    public static String numberToVietnameseWordsMoney(long number) {
        if (number == 0) return "Không đồng";

        String[] ChuSo = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        String[] DonVi = {"", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ", "tỷ tỷ"};

        StringBuilder result = new StringBuilder();
        int i = 0;

        while (number > 0) {
            int phan = (int) (number % 1000);
            if (phan > 0) {
                result.insert(0, docSo3ChuSo(phan, ChuSo) + " " + DonVi[i] + " ");
            }
            number /= 1000;
            i++;
        }

        String res = result.toString().trim();
        res = Character.toUpperCase(res.charAt(0)) + res.substring(1);
        return res + " đồng chẵn";
    }

    private static String docSo3ChuSo(int b, String[] ChuSo) {
        int tram = b / 100;
        int chuc = (b % 100) / 10;
        int donvi = b % 10;
        StringBuilder result = new StringBuilder();

        if (tram > 0) {
            result.append(ChuSo[tram]).append(" trăm");
            if (chuc == 0 && donvi > 0) result.append(" linh");
        }

        if (chuc > 0) {
            if (chuc == 1) result.append(" mười");
            else result.append(" ").append(ChuSo[chuc]).append(" mươi");
        }

        if (donvi > 0) {
            if (chuc > 1 && donvi == 1) result.append(" mốt");
            else if (donvi == 5 && chuc > 0) result.append(" lăm");
            else result.append(" ").append(ChuSo[donvi]);
        }

        return result.toString().trim();
    }


    private void fillPhuLucHanMucTable(XWPFTable table, TableRequest tableRequest) {
        if (table == null || tableRequest == null || !tableRequest.isDrawTable()) return;

        int numCols = tableRequest.getHeaders() != null ? tableRequest.getHeaders().size() : 0;

        // Set border toàn bảng
        CTTblBorders tblBorders = table.getCTTbl().getTblPr().isSetTblBorders()
                ? table.getCTTbl().getTblPr().getTblBorders()
                : table.getCTTbl().getTblPr().addNewTblBorders();
        setBorder(tblBorders.addNewInsideH());
        setBorder(tblBorders.addNewInsideV());
        setBorder(tblBorders.addNewTop());
        setBorder(tblBorders.addNewBottom());
        setBorder(tblBorders.addNewLeft());
        setBorder(tblBorders.addNewRight());

        // Header
        boolean hasHeader = tableRequest.getHeaders() != null && !tableRequest.getHeaders().isEmpty();
        if (hasHeader) {
            XWPFTableRow headerRow = table.createRow();
            ensureCells(headerRow, numCols);
            ensureParagraphsInRow(headerRow);
            for (int i = 0; i < numCols; i++) {
                setCellText(headerRow.getCell(i), tableRequest.getHeaders().get(i), true, false, true);
            }
            applyBordersToRow(headerRow);
        }

        // Rows
        for (List<String> rowData : tableRequest.getRows()) {
            XWPFTableRow row = table.createRow();
            ensureCells(row, numCols);
            ensureParagraphsInRow(row);
            for (int i = 0; i < numCols; i++) {
                String value = i < rowData.size() ? rowData.get(i) : "";
                setCellText(row.getCell(i), value, false, false, true);
            }
            applyBordersToRow(row);
        }

        // Merge (nếu có)
        if (tableRequest.getMerges() != null) {
            for (MergeInfoRequest merge : tableRequest.getMerges()) {
                int rowIndex = merge.getRowIndex();
                List<String> targets = merge.getMergeTargets();
                if (targets == null || targets.isEmpty()) continue;

                int startCol = Integer.parseInt(targets.get(0));
                int endCol = Integer.parseInt(targets.get(targets.size() - 1));

                int tableRowIndex = hasHeader ? rowIndex + 1 : rowIndex;
                if (tableRowIndex < 0 || tableRowIndex >= table.getNumberOfRows()) continue;

                XWPFTableCell baseCell = table.getRow(tableRowIndex).getCell(startCol);
                if (baseCell == null) continue;
                setCellText(baseCell, merge.getMergedValue() != null ? merge.getMergedValue() : "", false, true, true);
                CTTcPr tcPr = baseCell.getCTTc().isSetTcPr() ? baseCell.getCTTc().getTcPr() : baseCell.getCTTc().addNewTcPr();
                CTHMerge hMerge = tcPr.isSetHMerge() ? tcPr.getHMerge() : tcPr.addNewHMerge();
                hMerge.setVal(STMerge.RESTART);

                for (int c = startCol + 1; c <= endCol; c++) {
                    XWPFTableCell contCell = table.getRow(tableRowIndex).getCell(c);
                    if (contCell == null) continue;
                    while (contCell.getParagraphs().size() > 0) {
                        contCell.removeParagraph(0);
                    }
                    contCell.addParagraph();
                    CTTcPr tcPr2 = contCell.getCTTc().isSetTcPr() ? contCell.getCTTc().getTcPr() : contCell.getCTTc().addNewTcPr();
                    CTHMerge hMerge2 = tcPr2.isSetHMerge() ? tcPr2.getHMerge() : tcPr2.addNewHMerge();
                    hMerge2.setVal(STMerge.CONTINUE);
                }
            }
        }

        rebuildTableGrid(table, numCols);
    }


    private void calculateTyLeChoVay(Map<String, String> replacements, ContractRequest request, CreditContractTSBDRequest tsbdRequest) {
//        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        // Lấy giá trị tsbds
        long tsbds = parseLongSafe(Optional.ofNullable(tsbdRequest.getTongTaiSanBD()).orElse("0").replace(".", "").trim());

        // Lấy giá trị tienSo
        long tienSo = parseLongSafe(request.getTienSo());
        double tyLe = 0.0;
        if (tienSo > 0) {
            tyLe = ((double) tsbds / (double) tienSo) * 100;
        }

        // Format với 2 chữ số thập phân
//        DecimalFormat df = new DecimalFormat("#.##");
        safePutReplacement("{{tyLeChoVay}}", String.format(Locale.US, "%.2f", tyLe) + "%");
    }

    private static final String[] units = {
            "không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"
    };

    // Đọc số từ 1 đến 99 (dùng cho ngày, tháng)
    public static String numberToWords(int number) {
        if (number == 0) return units[0];
        if (number < 10) return units[number];
        if (number < 20) {
            if (number == 10) return "mười";
            if (number == 15) return "mười lăm";
            return "mười " + units[number % 10];
        }
        int tens = number / 10;
        int ones = number % 10;
        StringBuilder sb = new StringBuilder();
        sb.append(units[tens]).append(" mươi");
        if (ones == 1) sb.append(" mốt");
        else if (ones == 5) sb.append(" lăm");
        else if (ones > 0) sb.append(" ").append(units[ones]);
        return sb.toString();
    }

    // Đọc năm đầy đủ (ví dụ: 2026 → "hai nghìn không trăm hai mươi sáu")
    public static String readYearFull(int year) {
        int nghin = year / 1000;
        int tram = (year % 1000) / 100;
        int chuc = (year % 100) / 10;
        int donvi = year % 10;

        StringBuilder sb = new StringBuilder();

        // Nghìn
        sb.append(units[nghin]).append(" nghìn ");

        // Trăm
        sb.append(units[tram]).append(" trăm ");

        // Chục
        if (chuc == 0 && donvi != 0) {
            sb.append("lẻ ");
        } else if (chuc == 1) {
            sb.append("mười ");
        } else if (chuc > 1) {
            sb.append(units[chuc]).append(" mươi ");
        }

        // Đơn vị
        if (donvi > 0) {
            if (donvi == 1 && chuc > 1) {
                sb.append("mốt");
            } else if (donvi == 5 && chuc > 0) {
                sb.append("lăm");
            } else {
                sb.append(units[donvi]);
            }
        }

        return sb.toString().trim();
    }

    // Đọc ngày tháng năm thành chữ
    public static String dateToWords(LocalDate date) {
        String dayText = numberToWords(date.getDayOfMonth());
        String monthText = numberToWords(date.getMonthValue());
        String yearText = readYearFull(date.getYear());
        return "Ngày " + dayText + ", tháng " + monthText + ", năm " + yearText;
    }


    private Map<String, String> calculatePercents(double tongVonLuuDong,
                                                  double vonTuCo,
                                                  double vonKhac,
                                                  double tienSo,
                                                  boolean isThoaThuan) {
        // Nếu là thỏa thuận thì base = tongVonLuuDong / 1.2, ngược lại base = tongVonLuuDong
        double baseTotal = isThoaThuan ? tongVonLuuDong / 1.2 : tongVonLuuDong;

        Map<String, String> result = new HashMap<>();

        // Format phần trăm với 1 chữ số thập phân
        DecimalFormat percentFormat = new DecimalFormat("#0.0");

        result.put("vonTuCoPercent", baseTotal > 0 ? percentFormat.format((vonTuCo / baseTotal) * 100) + "%" : "0%");
        result.put("vonKhacPercent", baseTotal > 0 ? percentFormat.format((vonKhac / baseTotal) * 100) + "%" : "0%");
        result.put("tienSoPercent", baseTotal > 0 ? percentFormat.format((tienSo / baseTotal) * 100) + "%" : "0%");

        // Nếu là thỏa thuận thì thêm placeholder vonLuuDongMotVongQuay
        if (isThoaThuan) {
            // Format tiền theo dấu chấm ngăn cách hàng nghìn
            DecimalFormat moneyFormat = new DecimalFormat("#,###");
            result.put("vonLuuDongMotVongQuay", moneyFormat.format(tongVonLuuDong / 1.2));
        }

        return result;
    }


    private void insertTableAtPlaceholder(XWPFDocument doc,
                                          XWPFParagraph para,
                                          TableRequest tableRequest,
                                          boolean checkDrawTable,
                                          Map<String, String> replacements,
                                          ContractRequest request) {

        if (tableRequest == null) return;
        if (checkDrawTable && !tableRequest.isDrawTable()) return;

        // Tạo cursor từ paragraph placeholder
        XmlCursor cursor = para.getCTP().newCursor();

        // Chèn bảng
        XWPFTable table = doc.insertNewTbl(cursor);
        table.setTableAlignment(TableRowAlign.CENTER);

        // Xóa row mặc định nếu có
        if (table.getNumberOfRows() > 0) {
            table.removeRow(0);
        }

        // Fill dữ liệu theo loại bảng
        String tableType = tableRequest.getTableType();
        if (tableType == null) {
            fillGenericTable(table, tableRequest);
            return;
        }

        switch (tableType.toLowerCase()) {
            case "hanmuc":
                fillHanMucTable(table, tableRequest);
                break;
            case "thunhapdukien":
                fillThuNhapTable(table, tableRequest, replacements);
                break;
            case "chiphi":
                fillChiPhiTable(table, tableRequest, replacements, request);
                break;
            case "phuluchanmuc":
                fillPhuLucHanMucTable(table, tableRequest);
                break;
            default:
                fillGenericTable(table, tableRequest);
        }
    }


    private void fillGenericTable(XWPFTable table, TableRequest tableRequest) {
        if (table == null || tableRequest == null) return;

        // 👉 căn giữa toàn bộ bảng trên trang
        table.setTableAlignment(TableRowAlign.CENTER);

        // Xác định số cột
        int colCount = tableRequest.getHeaders() != null ? tableRequest.getHeaders().size() : 0;
        for (List<String> rowData : tableRequest.getRows()) {
            if (rowData.size() > colCount) colCount = rowData.size();
        }

        // Header nếu có
        if (tableRequest.getHeaders() != null && !tableRequest.getHeaders().isEmpty()) {
            XWPFTableRow headerRow = table.createRow();
            while (headerRow.getTableCells().size() < colCount) {
                XWPFTableCell cell = headerRow.addNewTableCell();
                // 👉 xoá paragraph rỗng mặc định
                if (!cell.getParagraphs().isEmpty()) {
                    cell.removeParagraph(0);
                }
                cell.addParagraph();
            }
            for (int c = 0; c < tableRequest.getHeaders().size(); c++) {
                XWPFTableCell cell = headerRow.getCell(c);
                if (!cell.getParagraphs().isEmpty()) {
                    cell.removeParagraph(0);
                }
                XWPFParagraph para = cell.addParagraph();
                para.setAlignment(ParagraphAlignment.CENTER); // 👉 căn giữa header
                XWPFRun run = para.createRun();
                run.setBold(true);
                run.setFontFamily("Times New Roman");
                run.setFontSize(13);
                run.setText(tableRequest.getHeaders().get(c));
            }
            applyBordersToRow(headerRow);
        }

        // Data rows
        for (List<String> rowData : tableRequest.getRows()) {
            XWPFTableRow row = table.createRow();
            while (row.getTableCells().size() < colCount) {
                XWPFTableCell cell = row.addNewTableCell();
                if (!cell.getParagraphs().isEmpty()) {
                    cell.removeParagraph(0);
                }
                cell.addParagraph();
            }
            for (int c = 0; c < colCount; c++) {
                String cellValue = c < rowData.size() ? rowData.get(c) : "";
                XWPFTableCell cell = row.getCell(c);
                if (!cell.getParagraphs().isEmpty()) {
                    cell.removeParagraph(0);
                }
                XWPFParagraph para = cell.addParagraph();
                para.setAlignment(ParagraphAlignment.CENTER); // 👉 căn giữa data
                XWPFRun run = para.createRun();
                run.setFontFamily("Times New Roman");
                run.setFontSize(13);
                run.setText(cellValue != null ? cellValue : "");
            }
            applyBordersToRow(row);
        }

        // Rebuild grid
        CTTbl ctTbl = table.getCTTbl();
        CTTblGrid tblGrid = ctTbl.getTblGrid() == null ? ctTbl.addNewTblGrid() : ctTbl.getTblGrid();
        while (tblGrid.sizeOfGridColArray() > 0) tblGrid.removeGridCol(0);
        for (int i = 0; i < colCount; i++) {
            tblGrid.addNewGridCol().setW(BigInteger.valueOf(2000));
        }
    }


    private String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        str = str.toLowerCase();
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }

    private void processParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null) {
                String replaced = text;
                for (Map.Entry<String, String> entry : replacements.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (key != null && value != null) {   // ensure both are non-null
                        replaced = replaced.replace(key, value);
                    }
                }

                if (!replaced.equals(text)) {
                    run.setText(replaced, 0); // giữ nguyên style của run
                }
            }
        }

        // Nếu toàn bộ paragraph rỗng sau khi thay thế → xóa paragraph
        if (paragraph.getText().trim().isEmpty()) {
            IBody body = paragraph.getBody();
            if (body instanceof XWPFDocument d) {
                int pos = d.getPosOfParagraph(paragraph);
                if (pos >= 0) d.removeBodyElement(pos);
            } else if (body instanceof XWPFTableCell cell) {
                int idx = cell.getParagraphs().indexOf(paragraph);
                if (idx >= 0) cell.removeParagraph(idx);
            }
        }

    }

    private void fillThuNhapTable(XWPFTable table,
                                  TableRequest tableRequest,
                                  Map<String, String> replacements) {
        if (table == null || tableRequest == null || !tableRequest.isDrawTable()) return;

        int colCount = tableRequest.getHeaders() != null ? tableRequest.getHeaders().size() : 0;

        // ===== Set border cho bảng =====
        CTTblBorders tblBorders = table.getCTTbl().getTblPr().isSetTblBorders()
                ? table.getCTTbl().getTblPr().getTblBorders()
                : table.getCTTbl().getTblPr().addNewTblBorders();
        setBorder(tblBorders.addNewInsideH());
        setBorder(tblBorders.addNewInsideV());
        setBorder(tblBorders.addNewTop());
        setBorder(tblBorders.addNewBottom());
        setBorder(tblBorders.addNewLeft());
        setBorder(tblBorders.addNewRight());

        // ===== Header =====
        boolean hasHeader = tableRequest.getHeaders() != null && !tableRequest.getHeaders().isEmpty();
        if (hasHeader) {
            XWPFTableRow headerRow = table.createRow();
            ensureCells(headerRow, colCount);
            ensureParagraphsInRow(headerRow);
            for (int c = 0; c < colCount; c++) {
                setCellText(headerRow.getCell(c), tableRequest.getHeaders().get(c), true, false, true);
            }
            applyBordersToRow(headerRow);
        }

        // ===== Data rows =====
//        DecimalFormat df = new DecimalFormat("#,###");
        for (List<String> rowData : tableRequest.getRows()) {
            XWPFTableRow row = table.createRow();
            ensureCells(row, colCount);
            ensureParagraphsInRow(row);
            for (int c = 0; c < colCount; c++) {
                String cellValue = c < rowData.size() ? rowData.get(c) : "";

                // Format số cho các cột số lượng, đơn giá, thành tiền
                if (c == 2 || c == 3 || c == 4) {
                    long number = parseLongSafe(cellValue);
                    if (number > 0) {
                        cellValue = formatCurrency(number);
                    }
                }

                setCellText(row.getCell(c), cellValue, false, false, true);
            }
            applyBordersToRow(row);
        }

        // ===== Tính tổng và gán vào hàng cuối =====
        long tongThuNhap = 0;
        for (List<String> rowData : tableRequest.getRows()) {
            if (rowData.size() > 4 && !"Tổng cộng:".equals(rowData.get(0))) {
                tongThuNhap += parseLongSafe(rowData.get(4));
            }
        }
        List<String> lastRow = tableRequest.getRows().get(tableRequest.getRows().size() - 1);
        lastRow.set(4, formatCurrency(tongThuNhap));
        System.err.println("tong doanh thu --> " + tongThuNhap);

        // 👉 Gán vào replacements để dùng cho placeholder {{tongDoanhThu}}
        safePutReplacement("{{tongDoanhThu}}", formatCurrency(tongThuNhap));

        // ===== Merge hàng cuối (Tổng cộng) =====
        Map<String, Integer> colIndexMap = new HashMap<>();
        colIndexMap.put("noiDung", 0);
        colIndexMap.put("donVi", 1);
        colIndexMap.put("soLuong", 2);
        colIndexMap.put("donGia", 3);
        colIndexMap.put("thanhTien", 4);

        if (tableRequest.getMerges() != null) {
            for (MergeInfoRequest merge : tableRequest.getMerges()) {
                int rowIndex = merge.getRowIndex();
                List<String> targets = merge.getMergeTargets();
                if (targets == null || targets.isEmpty()) continue;

                int startCol = colIndexMap.getOrDefault(targets.get(0), 0);
                int endCol = colIndexMap.getOrDefault(targets.get(targets.size() - 1), startCol);

                int tableRowIndex = hasHeader ? rowIndex + 1 : rowIndex;
                if (tableRowIndex < 0 || tableRowIndex >= table.getNumberOfRows()) continue;

                XWPFTableRow mergedRow = table.getRow(tableRowIndex);

                // In đậm toàn bộ hàng merge
                for (int c = 0; c < mergedRow.getTableCells().size(); c++) {
                    XWPFTableCell cell = mergedRow.getCell(c);
                    if (cell != null) {
                        String text = cell.getText();
                        setCellText(cell, text, false, true, true);
                    }
                }

                // Xử lý merge
                XWPFTableCell baseCell = mergedRow.getCell(startCol);
                if (baseCell == null) continue;
                setCellText(baseCell, merge.getMergedValue() != null ? merge.getMergedValue() : "", false, true, true);
                CTTcPr tcPr = baseCell.getCTTc().isSetTcPr() ? baseCell.getCTTc().getTcPr() : baseCell.getCTTc().addNewTcPr();
                CTHMerge hMerge = tcPr.isSetHMerge() ? tcPr.getHMerge() : tcPr.addNewHMerge();
                hMerge.setVal(STMerge.RESTART);

                for (int c = startCol + 1; c <= endCol; c++) {
                    XWPFTableCell contCell = mergedRow.getCell(c);
                    if (contCell == null) continue;
                    while (contCell.getParagraphs().size() > 0) {
                        contCell.removeParagraph(0);
                    }
                    contCell.addParagraph();
                    CTTcPr tcPr2 = contCell.getCTTc().isSetTcPr() ? contCell.getCTTc().getTcPr() : contCell.getCTTc().addNewTcPr();
                    CTHMerge hMerge2 = tcPr2.isSetHMerge() ? tcPr2.getHMerge() : tcPr2.addNewHMerge();
                    hMerge2.setVal(STMerge.CONTINUE);
                }
            }
        }

        // Tính lợi nhuận sau khi có tổng doanh thu
        calculateLoiNhuan();

        // ===== Rebuild grid =====
        rebuildTableGrid(table, colCount);
    }


    private void calculateLoiNhuan() {

        // Parse số an toàn
        long tongDoanhThu = parseLongSafe(replacements.get("{{tongDoanhThu}}"));
        long tongChiPhi = parseLongSafe(replacements.get("{{tongChiPhi}}"));
        System.err.println("tổng chi phí trên lợi nhuận: " + tongChiPhi);
        // Tính lợi nhuận
        long loiNhuan = tongDoanhThu - tongChiPhi;
        System.err.println("Lợi nhuận --> " + loiNhuan);

        // Dùng DecimalFormat thay cho NumberFormat để không phụ thuộc locale
        DecimalFormat df = new DecimalFormat("#,###");

        // Ghi vào replacements
        safePutReplacement("{{loiNhuan}}", df.format(loiNhuan));

        // Thêm placeholder mới: lợi nhuận năm trước (ước tính)
        long loiNhuanNamTruoc = Math.round(loiNhuan / 1.15);
        safePutReplacement("{{loiNhuanNamTruoc}}", df.format(loiNhuanNamTruoc));

        // Thêm placeholder mới: lợi nhuận dự kiến = lợi nhuận - lợi nhuận năm trước
        long loiNhuanDuKien = loiNhuan - loiNhuanNamTruoc;
        safePutReplacement("{{loiNhuanDuKien}}", df.format(loiNhuanDuKien));
    }


    // ======= Hàm chính: fillHanMucTable =======
    private void fillHanMucTable(XWPFTable table, TableRequest tableRequest) {
        if (table == null || tableRequest == null || !tableRequest.isDrawTable()) return;

        int numCols = tableRequest.getHeaders() != null ? tableRequest.getHeaders().size() : 0;

        // ===== Set border cho bảng (toàn bảng) =====
        CTTblBorders tblBorders = table.getCTTbl().getTblPr().isSetTblBorders()
                ? table.getCTTbl().getTblPr().getTblBorders()
                : table.getCTTbl().getTblPr().addNewTblBorders();
        setBorder(tblBorders.addNewInsideH());
        setBorder(tblBorders.addNewInsideV());
        setBorder(tblBorders.addNewTop());
        setBorder(tblBorders.addNewBottom());
        setBorder(tblBorders.addNewLeft());
        setBorder(tblBorders.addNewRight());

        // ===== Header (nếu có) =====
        boolean hasHeader = tableRequest.getHeaders() != null && !tableRequest.getHeaders().isEmpty();
        if (hasHeader) {
            XWPFTableRow headerRow = table.createRow();
            ensureCells(headerRow, numCols);
            // đảm bảo mỗi cell có paragraph
            ensureParagraphsInRow(headerRow);
            for (int i = 0; i < numCols; i++) {
                setCellText(headerRow.getCell(i), tableRequest.getHeaders().get(i), true, false, true);
            }
            applyBordersToRow(headerRow);
        }

        // ===== Dữ liệu =====
        for (List<String> rowData : tableRequest.getRows()) {
            XWPFTableRow row = table.createRow();
            ensureCells(row, numCols);
            ensureParagraphsInRow(row);
            for (int i = 0; i < numCols; i++) {
                String value = i < rowData.size() ? rowData.get(i) : "";
                // luôn gọi setCellText để tạo paragraph + run (dù rỗng)
                setCellText(row.getCell(i), value, false, false, true);
            }
            applyBordersToRow(row);
        }

        // ===== Merge (nếu có) =====
        if (tableRequest.getMerges() != null) {
            for (MergeInfoRequest merge : tableRequest.getMerges()) {
                int rowIndex = merge.getRowIndex();
                List<String> targets = merge.getMergeTargets();
                if (targets == null || targets.isEmpty()) continue;

                int startCol = Integer.parseInt(targets.get(0));
                int endCol = Integer.parseInt(targets.get(targets.size() - 1));

                // Tính index thực tế trong XWPFTable: nếu có header thì +1
                int tableRowIndex = hasHeader ? rowIndex + 1 : rowIndex;
                if (tableRowIndex < 0 || tableRowIndex >= table.getNumberOfRows()) continue;

                // Cell RESTART: set text (ghi đè)
                XWPFTableCell baseCell = table.getRow(tableRowIndex).getCell(startCol);
                // đảm bảo baseCell tồn tại
                if (baseCell == null) continue;
                setCellText(baseCell, merge.getMergedValue() != null ? merge.getMergedValue() : "", false, true, true);
                CTTcPr tcPr = baseCell.getCTTc().isSetTcPr() ? baseCell.getCTTc().getTcPr() : baseCell.getCTTc().addNewTcPr();
                CTHMerge hMerge = tcPr.isSetHMerge() ? tcPr.getHMerge() : tcPr.addNewHMerge();
                hMerge.setVal(STMerge.RESTART);

                // Cell CONTINUE: xóa run, thêm paragraph rỗng (bắt buộc), set CONTINUE
                for (int c = startCol + 1; c <= endCol; c++) {
                    XWPFTableCell contCell = table.getRow(tableRowIndex).getCell(c);
                    if (contCell == null) continue;
                    // xóa tất cả paragraph cũ
                    while (contCell.getParagraphs().size() > 0) {
                        contCell.removeParagraph(0);
                    }
                    // thêm paragraph rỗng (bắt buộc để tránh lỗi XML)
                    contCell.addParagraph();
                    // đảm bảo không có run chứa text
                    CTTcPr tcPr2 = contCell.getCTTc().isSetTcPr() ? contCell.getCTTc().getTcPr() : contCell.getCTTc().addNewTcPr();
                    CTHMerge hMerge2 = tcPr2.isSetHMerge() ? tcPr2.getHMerge() : tcPr2.addNewHMerge();
                    hMerge2.setVal(STMerge.CONTINUE);
                }
            }
        }

        // ===== Rebuild grid (đồng bộ số cột) =====
        rebuildTableGrid(table, numCols);
    }

    private void fillChiPhiTable(XWPFTable table, TableRequest tableRequest,
                                 Map<String, String> replacements, ContractRequest request) {
        if (table == null || tableRequest == null || !tableRequest.isDrawTable()) return;

        int colCount = tableRequest.getHeaders() != null ? tableRequest.getHeaders().size() : 0;

        // ===== Set border cho bảng =====
        CTTblBorders tblBorders = table.getCTTbl().getTblPr().isSetTblBorders()
                ? table.getCTTbl().getTblPr().getTblBorders()
                : table.getCTTbl().getTblPr().addNewTblBorders();
        setBorder(tblBorders.addNewInsideH());
        setBorder(tblBorders.addNewInsideV());
        setBorder(tblBorders.addNewTop());
        setBorder(tblBorders.addNewBottom());
        setBorder(tblBorders.addNewLeft());
        setBorder(tblBorders.addNewRight());

        // ===== Header =====
        boolean hasHeader = tableRequest.getHeaders() != null && !tableRequest.getHeaders().isEmpty();
        if (hasHeader) {
            XWPFTableRow headerRow = table.createRow();
            ensureCells(headerRow, colCount);
            ensureParagraphsInRow(headerRow);
            for (int c = 0; c < colCount; c++) {
                boolean alignCenter = (c != 1); // cột 2 giữ lề trái
                setCellText(headerRow.getCell(c), tableRequest.getHeaders().get(c), true, false, alignCenter);
            }
            applyBordersToRow(headerRow);
        }

        // ===== Data rows =====
//        DecimalFormat df = new DecimalFormat("#.###");
        for (List<String> rowData : tableRequest.getRows()) {
            XWPFTableRow row = table.createRow();
            ensureCells(row, colCount);
            ensureParagraphsInRow(row);
            for (int c = 0; c < colCount; c++) {
                String cellValue = c < rowData.size() ? rowData.get(c) : "";

                if (c == 3 || c == 4 || c == 5) {
                    long number = parseLongSafe(cellValue);
                    if (number > 0) {
                        cellValue = formatCurrency(number);
                    }
                }

                boolean alignCenter = (c != 1);
                setCellText(row.getCell(c), cellValue, false, false, alignCenter);
            }
            applyBordersToRow(row);
        }

        // ===== Merge =====
        if (tableRequest.getMerges() != null) {
            Map<String, Integer> colIndexMap = new HashMap<>();
            colIndexMap.put("stt", 0);
            colIndexMap.put("danhMuc", 1);
            colIndexMap.put("donVi", 2);
            colIndexMap.put("soLuong", 3);
            colIndexMap.put("donGia", 4);
            colIndexMap.put("thanhTien", 5);

            for (MergeInfoRequest merge : tableRequest.getMerges()) {
                int rowIndex = merge.getRowIndex();
                List<String> targets = merge.getMergeTargets();
                if (targets == null || targets.isEmpty()) continue;

                int startCol = colIndexMap.getOrDefault(targets.get(0), 0);
                int endCol = colIndexMap.getOrDefault(targets.get(targets.size() - 1), startCol);

                int tableRowIndex = hasHeader ? rowIndex + 1 : rowIndex;
                if (tableRowIndex < 0 || tableRowIndex >= table.getNumberOfRows()) continue;

                XWPFTableRow mergedRow = table.getRow(tableRowIndex);

                // In đậm toàn bộ hàng merge
                for (int c = 0; c < mergedRow.getTableCells().size(); c++) {
                    XWPFTableCell cell = mergedRow.getCell(c);
                    if (cell != null) {
                        String text = cell.getText();
                        boolean alignCenter = (c != 1);
                        setCellText(cell, text, false, true, alignCenter);
                    }
                }

                // Xử lý merge
                XWPFTableCell baseCell = mergedRow.getCell(startCol);
                if (baseCell == null) continue;
                setCellText(baseCell, merge.getMergedValue() != null ? merge.getMergedValue() : "", false, true, (startCol != 1));
                CTTcPr tcPr = baseCell.getCTTc().isSetTcPr() ? baseCell.getCTTc().getTcPr() : baseCell.getCTTc().addNewTcPr();
                CTHMerge hMerge = tcPr.isSetHMerge() ? tcPr.getHMerge() : tcPr.addNewHMerge();
                hMerge.setVal(STMerge.RESTART);

                for (int c = startCol + 1; c <= endCol; c++) {
                    XWPFTableCell contCell = mergedRow.getCell(c);
                    if (contCell == null) continue;
                    while (contCell.getParagraphs().size() > 0) {
                        contCell.removeParagraph(0);
                    }
                    contCell.addParagraph();
                    CTTcPr tcPr2 = contCell.getCTTc().isSetTcPr() ? contCell.getCTTc().getTcPr() : contCell.getCTTc().addNewTcPr();
                    CTHMerge hMerge2 = tcPr2.isSetHMerge() ? tcPr2.getHMerge() : tcPr2.addNewHMerge();
                    hMerge2.setVal(STMerge.CONTINUE);
                }
            }
        }

        // ===== Rebuild grid =====
        rebuildTableGrid(table, colCount);

        // ===== Tính toán {{tongNCV}}, {{tongChiPhi}}, {{vonTuCo}}, {{phanTramVTC}}, {{phanTramVV}} =====
        long tongChiPhi = 0;
        long chiPhiGianTiep = 0;

        if (tableRequest.getMerges() != null) {
            for (MergeInfoRequest merge : tableRequest.getMerges()) {
                String mergedValue = merge.getMergedValue();
                if (mergedValue != null) {
                    if (mergedValue.toLowerCase().contains("tổng chi phí")) {
                        List<String> row = tableRequest.getRows().get(merge.getRowIndex());
                        tongChiPhi = parseLongSafe(row.get(5));
                    }
                    if (mergedValue.toLowerCase().contains("chi phí gián tiếp")) {
                        List<String> row = tableRequest.getRows().get(merge.getRowIndex());
                        chiPhiGianTiep = parseLongSafe(row.get(5));
                    }
                }
            }
        }
        System.err.println("tổng chi phí: " + tongChiPhi);
        long tongNCV = tongChiPhi - chiPhiGianTiep;
        safePutReplacement("{{tongNCV}}", formatCurrency(tongNCV));
        safePutReplacement("{{tongChiPhi}}", formatCurrency(tongChiPhi));

        long tienSo = parseLongSafe(request.getTienSo());

// Lấy hệ số vonTuCo từ Frontend (ví dụ: 40 nghĩa là 40%)
        double heSoVonKhac = request.getPavvRequest() != null && request.getPavvRequest().getHeSoVonKhac() != null
                ? request.getPavvRequest().getHeSoVonKhac()
                : 0.0;
        long vonTuCo = 0;
        long vonKhac = 0;
        double phanTramVV = 0.0;
        long vonLuuDong = 0;
        if (request.getLoaiVay().equalsIgnoreCase(getAnotherString())) {
            vonLuuDong = Math.round(tongNCV / 1.2);
            vonKhac = Math.round(vonLuuDong * heSoVonKhac / 100.0);
            vonTuCo = Math.max(0, vonLuuDong - tienSo - vonKhac);
            phanTramVV = vonLuuDong > 0 ? (double) tienSo / vonLuuDong * 100 : 0;
            safePutReplacement("{{vonLuuDong}}", formatCurrency(vonLuuDong));
        } else {
            vonKhac = Math.round(tongNCV * heSoVonKhac / 100.0);
            vonTuCo = Math.max(0, tongNCV - tienSo - vonKhac);
            phanTramVV = tongNCV > 0 ? (double) tienSo / tongNCV * 100 : 0;
            safePutReplacement("{{vonLuuDong}}", "");
        }
        double phanTramVonKhac = heSoVonKhac; // lấy trực tiếp từ Frontend
        double phanTramVTC = Math.max(0, 100 - phanTramVV - phanTramVonKhac);
// Gán placeholders
        safePutReplacement("{{vonTuCo}}", formatCurrency(vonTuCo));
        safePutReplacement("{{vonKhac}}", formatCurrency(vonKhac));
        safePutReplacement("{{phanTramVTC}}", String.format("%.1f", phanTramVTC) + "%");
        safePutReplacement("{{phanTramVV}}", String.format("%.1f", phanTramVV) + "%");
        safePutReplacement("{{phanTramVonKhac}}", String.format("%.1f", phanTramVonKhac) + "%");
    }

    private static String getAnotherString() {
        return "NGẮN HẠN (Thỏa thuận)";
    }


    // Hàm phụ để parse số an toàn
    private long parseLongSafe(String value) {
        try {
            if (value == null) return 0;
            // Loại bỏ dấu chấm, dấu phẩy, khoảng trắng
            String cleaned = value.replace(".", "")
                    .replace(",", "")
                    .trim();
            return Long.parseLong(cleaned);
        } catch (Exception e) {
            return 0;
        }
    }


    // ======= Hàm phụ dùng chung =======
    // Đảm bảo row có đủ số cell
    private void ensureCells(XWPFTableRow row, int numCols) {
        int existing = row.getTableCells().size();
        for (int i = existing; i < numCols; i++) {
            row.addNewTableCell();
        }
    }

    // Đảm bảo mỗi cell trong row có ít nhất một paragraph (tránh thiếu <p>)
    private void ensureParagraphsInRow(XWPFTableRow row) {
        for (XWPFTableCell cell : row.getTableCells()) {
            if (cell.getParagraphs() == null || cell.getParagraphs().isEmpty()) {
                cell.addParagraph();
            }
        }
    }

    // Hàm put replacement an toàn
    // Escape ký tự đặc biệt XML để tránh corrupt file Word
    private void safePutReplacement(String key, String value) {
        if (value == null || value.isBlank()) {
            replacements.put(key, "");
            return;
        }
        String safe = value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
        replacements.put(key, safe);
    }


    private void setCellText(XWPFTableCell cell, String text, boolean isHeader, boolean bold, boolean alignCenter) {
        if (cell == null) return;
        while (cell.getParagraphs().size() > 0) {
            cell.removeParagraph(0);
        }
        XWPFParagraph p = cell.addParagraph();
        XWPFRun run = p.createRun();
        run.setText(text != null ? text : "");
        run.setFontFamily("Times New Roman"); // 👉 fix font
        run.setFontSize(14);                  // 👉 optional: set size chuẩn
        if (bold) run.setBold(true);

        if (alignCenter) {
            p.setAlignment(ParagraphAlignment.CENTER);
        } else {
            p.setAlignment(ParagraphAlignment.LEFT);
        }
    }


    // Set border cho CTBorder
    private void setBorder(CTBorder border) {
        border.setVal(STBorder.SINGLE);
        border.setSz(BigInteger.valueOf(4));
        border.setColor("000000");
    }

    // Áp border cho từng cell trong row
    private void applyBordersToRow(XWPFTableRow row) {
        for (XWPFTableCell cell : row.getTableCells()) {
            CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
            CTTcBorders borders = tcPr.isSetTcBorders() ? tcPr.getTcBorders() : tcPr.addNewTcBorders();

            CTBorder top = borders.isSetTop() ? borders.getTop() : borders.addNewTop();
            top.setVal(STBorder.SINGLE);
            top.setSz(BigInteger.valueOf(4));
            top.setColor("000000");

            CTBorder bottom = borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom();
            bottom.setVal(STBorder.SINGLE);
            bottom.setSz(BigInteger.valueOf(4));
            bottom.setColor("000000");

            CTBorder left = borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft();
            left.setVal(STBorder.SINGLE);
            left.setSz(BigInteger.valueOf(4));
            left.setColor("000000");

            CTBorder right = borders.isSetRight() ? borders.getRight() : borders.addNewRight();
            right.setVal(STBorder.SINGLE);
            right.setSz(BigInteger.valueOf(4));
            right.setColor("000000");
        }
    }

    /**
     * Scan all tables/cells in the document, report cells missing <p> and fix them by adding an empty paragraph.
     * Call this right before writing the document to disk.
     */
    private void fixTablesEnsureParagraphs(XWPFDocument doc) {
        if (doc == null) return;

        int tableIndex = 0;
        for (XWPFTable table : doc.getTables()) {
            int rowIndex = 0;
            for (XWPFTableRow row : table.getRows()) {
                int colIndex = 0;
                for (XWPFTableCell cell : row.getTableCells()) {
                    // Direct low-level check: CT_Tc p list
                    CTTc ctTc = cell.getCTTc();
                    // If getPList is empty -> no <p> elements
                    if (ctTc.getPList() == null || ctTc.getPList().isEmpty()) {
                        // Log for debugging
                        System.err.println(String.format("fixTablesEnsureParagraphs: table=%d row=%d col=%d -> missing <p>, adding one", tableIndex, rowIndex, colIndex));
                        // Add a paragraph safely
                        // Remove any stray paragraphs (defensive)
                        while (cell.getParagraphs().size() > 0) {
                            cell.removeParagraph(0);
                        }
                        // Add an empty paragraph
                        XWPFParagraph p = cell.addParagraph();
                        // Optionally add an empty run to be extra-safe (Word accepts empty <p>, but some versions like a run)
                        XWPFRun r = p.createRun();
                        r.setText(""); // empty text
                        // ensure font consistent (optional)
                        r.setFontFamily("Times New Roman");
                        r.setFontSize(12);
                    } else {
                        // Defensive: ensure at least one paragraph has a run or exists; if paragraphs exist but all empty it's OK.
                        // But also ensure cell has tcPr
                        if (!ctTc.isSetTcPr()) {
                            ctTc.addNewTcPr();
                        }
                    }
                    colIndex++;
                }
                rowIndex++;
            }
            tableIndex++;
        }
    }

    // Rebuild table grid để đồng bộ số cột (tránh Word tự sửa)
    private void rebuildTableGrid(XWPFTable table, int colCount) {
        CTTbl ctTbl = table.getCTTbl();
        CTTblGrid tblGrid = ctTbl.getTblGrid() == null ? ctTbl.addNewTblGrid() : ctTbl.getTblGrid();
        // xóa hết grid cũ
        while (tblGrid.sizeOfGridColArray() > 0) tblGrid.removeGridCol(0);
        for (int i = 0; i < colCount; i++) {
            tblGrid.addNewGridCol().setW(BigInteger.valueOf(2000));
        }
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

    private void expandTablesFullWidth(XWPFDocument doc) {
        for (XWPFTable table : doc.getTables()) {
            CTTblPr tblPr = table.getCTTbl().getTblPr();
            if (tblPr == null) {
                tblPr = table.getCTTbl().addNewTblPr();
            }
            CTTblWidth tblWidth = tblPr.getTblW();
            if (tblWidth == null) {
                tblWidth = tblPr.addNewTblW();
            }
            // Đặt chiều rộng bảng = 100% trang
            tblWidth.setType(STTblWidth.PCT);
            tblWidth.setW(BigInteger.valueOf(5000)); // 5000 = 100% theo chuẩn Word XML
            table.setTableAlignment(TableRowAlign.CENTER);
        }
    }

}
