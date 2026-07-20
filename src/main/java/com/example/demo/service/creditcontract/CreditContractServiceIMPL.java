package com.example.demo.service.creditcontract;

import com.example.demo.dto.request.*;
import com.example.demo.mapper.ContractMapper;
import com.example.demo.model.CreditContractEntity;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;

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
import java.time.format.DateTimeFormatter;
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
        LocalDate dateTC = LocalDate.parse(request.getNgayTheChap());
        LocalDate dateBD = LocalDate.parse(request.getNgayBaoDam());

        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "HopDongTinDung.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "HopDongTheChap.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "PhieuBaoDamQSDD.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "GiayDeNghiVayVon.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "DanhMucHoSoChoVay.docx"));
//        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "PhuLucHopDong.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "BienBanKiemTraSauKhiChoVay.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "BienBanXetDuyetChoVay.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "BienBanXacDinhGiaTriTaiSanBaoDam.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "PhuongAnVayVon.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "BaoCaoDeNghiGiaiNganKiemGiayNhanNo.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "BaoCaoThongTinVeNguoiCoLienQuan.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "ThongBao.docx"));
        return fileUrls;
    }

    // 👉 Export: tạo mới hợp đồng và lưu DB
    @Transactional
    public List<String> generateContractFilesExport(ContractRequest request) throws IOException {
        User user = userDetailService.getCurrentUser();
        LocalDate date = LocalDate.parse(request.getContractDate());
        LocalDate dateTC = LocalDate.parse(request.getNgayTheChap());
        LocalDate dateBD = LocalDate.parse(request.getNgayBaoDam());

        CreditContractEntity entity = new CreditContractEntity();
        contractMapper.mapRequestToEntity(request, entity, user, date, dateTC, dateBD);
        contractMapper.processAvatars(request, entity, tempDir, uploadDir, fileMetadataRepository);

        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "HopDongTinDung.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "HopDongTheChap.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "PhieuBaoDamQSDD.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "GiayDeNghiVayVon.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "DanhMucHoSoChoVay.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BienBanKiemTraSauKhiChoVay.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BienBanXetDuyetChoVay.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BienBanXacDinhGiaTriTaiSanBaoDam.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "PhuongAnVayVon.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BaoCaoDeNghiGiaiNganKiemGiayNhanNo.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BaoCaoThongTinVeNguoiCoLienQuan.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "ThongBao.docx"));

        creditContractRepository.save(entity);
        return fileUrls;
    }

    // 👉 Export: update hợp đồng đã có
    @Transactional
    public List<String> updateContractFilesExport(Long id, ContractRequest request) throws IOException {
        User user = userDetailService.getCurrentUser();
        LocalDate date = LocalDate.parse(request.getContractDate());
        LocalDate dateTC = LocalDate.parse(request.getNgayTheChap());
        LocalDate dateBD = LocalDate.parse(request.getNgayBaoDam());

        CreditContractEntity entity = creditContractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        contractMapper.mapRequestToEntity(request, entity, user, date, dateTC, dateBD);
        contractMapper.processAvatars(request, entity, tempDir, uploadDir, fileMetadataRepository);

        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "HopDongTinDung.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "HopDongTheChap.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "PhieuBaoDamQSDD.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "GiayDeNghiVayVon.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "DanhMucHoSoChoVay.docx"));
//        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "PhuLucHopDong.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BienBanKiemTraSauKhiChoVay.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BienBanXetDuyetChoVay.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BienBanXacDinhGiaTriTaiSanBaoDam.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "PhuongAnVayVon.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BaoCaoDeNghiGiaiNganKiemGiayNhanNo.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BaoCaoThongTinVeNguoiCoLienQuan.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "ThongBao.docx"));

        creditContractRepository.save(entity);
        return fileUrls;
    }

    // 👉 Hàm generate file (preview)
    private String generateContractFile(ContractRequest request, LocalDate date, LocalDate dateTC, LocalDate dateBD, User user, String templateName) throws IOException {
        try (InputStream is = new ClassPathResource("templates/" + templateName).getInputStream();
             XWPFDocument doc = new XWPFDocument(is)) {

            replacePlaceholders(doc, request, date, dateTC, dateBD);
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
                                              @NotNull LocalDate dateBD,
                                              @NotNull User user,
                                              @NotNull String templateName) throws IOException {
        try (InputStream is = new ClassPathResource("templates/" + templateName).getInputStream();
             XWPFDocument doc = new XWPFDocument(is)) {

            replacePlaceholders(doc, request, date, dateTC, dateBD);
            fixTablesEnsureParagraphs(doc);
            expandTablesFullWidth(doc);
            // centerTableContent(doc); // nếu muốn căn giữa nội dung

            String fileName = templateName.replace(".docx", "")
                    + "_export_" + user.getId()
                    + "_" + System.currentTimeMillis()
                    + ".docx";

            Path outputPath = Paths.get(contractFilesDir, fileName);
            Files.createDirectories(outputPath.getParent());

            try (OutputStream os = Files.newOutputStream(outputPath)) {
                doc.write(os);
            }

            return outputPath.toString();
        }
    }


    private void replacePlaceholders(XWPFDocument doc, ContractRequest request, LocalDate date, LocalDate dateTC, LocalDate dateBD) {
        System.err.println("request --> " + request);
        System.err.println("date ::::" + date);
        System.err.println("date ::::" + dateToWords(date));
        LocalDate dateKT = date.plusYears(Long.parseLong(request.getThoiHanVay()));
        System.err.println("dateKT ::::" + dateKT);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String ngayKetThuc = dateKT.format(formatter);
        long gtqsdd = Long.parseLong(Optional.ofNullable(request.getGiaTriQuyenSuDungDat()).orElse(String.valueOf(0L)));
// Định dạng theo locale Việt Nam
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedGtqsdd = nf.format(gtqsdd);
        Map<String, String> replacements = new HashMap<>(); // Các placeholder mặc định
//        replacements.put("{{gd}}", Optional.ofNullable(request.getNguoiDaiDien()).orElse(""));

        replacements.put("{{dateTextWords}}", dateToWords(date));
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
        replacements.put("{{tdp}}", Optional.ofNullable(request.getDiaChiThuongTruKhachHang().split(",")[0].trim()).orElse(""));
        replacements.put("{{gtnt}}", Optional.ofNullable(request.getGtnt()).orElse(""));
        replacements.put("{{gtntt}}", Optional.ofNullable(request.getGtnt().toLowerCase()).orElse(""));
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
        replacements.put("{{sbbxdcv}}", Optional.ofNullable(request.getSoBBXetDuyetChoVay()).orElse(""));
        replacements.put("{{ls}}", Optional.ofNullable(request.getLaiSuat()).orElse(""));
        replacements.put("{{nkt}}", Optional.ofNullable(ngayKetThuc).orElse(""));
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
        if (request.getCheckNguonGocSuDung()) {
            replacements.put("{{ngsd}}", Optional.ofNullable("Nguồn gốc sử dụng: " + request.getNguonGocSuDung()).orElse(""));
        } else {
            replacements.put("{{ngsd}}", "");
        }
        replacements.put("{{gc}}", Optional.ofNullable(request.getGhiChu()).orElse(""));
        replacements.put("{{chv}}", Optional.ofNullable(request.getChoVay()).orElse(""));
        replacements.put("{{gtqsdd}}", Optional.ofNullable(formattedGtqsdd).orElse(""));
        replacements.put("{{khbd}}", Optional.ofNullable(request.getDungTenBiaDo1()).orElse(""));
        replacements.put("{{ndnb}}", Optional.ofNullable(request.getNoiDungNgoaiBia()).orElse(""));
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
        replacements.put("{{tgvv}}", Optional.ofNullable(request.getHanMuc()).orElse(""));
        if (request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)")) {
            replacements.put("{{lvt}}", Optional.ofNullable("Ngắn hạn").orElse(""));
            replacements.put("{{slv}}", "hạn mức");
            System.err.println("get::" + replacements.get("{{slv}}"));
            replacements.put("{{ms1t}}", "Phương thức cho vay: Cho vay theo hạn mức");
            replacements.put("{{ms1d}}", "");
            replacements.put("{{ms2t}}", "Hạn mức cho vay:");
            replacements.put("{{ms2d}}", "Bên A cam kết cho bên B vay các khoản cấp tín dụng bằng đồng Việt Nam với hạn mức cho vay là: " + request.getTienSo() + " đồng, (Bằng chữ: " + request.getTienChu() + " )");
            replacements.put("{{ms3}}", "Mục đích sử dụng tiền vay: " + request.getMuchDichVay());
            replacements.put("{{ms4}}", "Thời hạn duy trì hạn mức: " + request.getHanMuc() + ", kể từ ngày ký thỏa thuận Hợp đồng tín dụng này. Trong khoảng thời gian này Bên B được đề nghị Bên A cấp tín dụng phù hợp với mục đích sử dụng vốn và có thể đề nghị giải ngân một lần hoặc nhiều lần trong hạn mức nêu tại Hợp đồng tín dụng này. Hết thời hạn duy trì hạn mức hợp đồng này, bên A không có nghĩa vụ giải ngân bất kỳ khoản vay nợ nào");
            replacements.put("{{ms5}}", "5. Một năm ít nhất một lần bên A có trách nhiệm xem xét, xác định lại hạn mức cho vay tối đa và thời gian duy trì hạn mức Hợp đồng này.");
            replacements.put("{{ms6t}}", "6. Thời hạn cho vay: Từng khoản cấp tín dụng được xác định cụ thể trên từng giấy nhận nợ, mỗi giấy nhận nợ có thời gian cho vay khác nhau và được Bên A xác định vào chu kỳ sản xuất kinh doanh, khả năng trả nợ của Bên B và không vượt quá 10 tháng hoặc không vượt quá một thời hạn khác do Bên A xác định trong từng thời kỳ.");
            replacements.put("{{ms6d}}", "Thời hạn cho vay của từng khoản cấp tín dụng cụ thể được tính từ ngày tiếp theo của ngày giải ngân cho đến thời điểm trả hết toàn bộ tiền gốc, lãi tiền vay và các chi phí phát sinh liên quan. Trong trường hợp Bên B sử dụng tiền vay chưa đủ một ngày, thì tính từ thời điểm nhận tiền vay và thời gian vay vốn được tính là 01 (một) ngày và trường hợp ngày cuối cùng của thời hạn vay là ngày lễ hoặc thứ 7, chủ nhật hàng tuần, thì ngày đến hạn chuyển sang ngày làm việc tiếp theo.");
            replacements.put("{{tghm}}", "Thời gian xác định bình quân cho một chu kỳ sản xuất, kinh doanh.");
            replacements.put("{{vqhm}}", "Vòng quay vốn lưu động = Tổng số ngày 01 năm/Tổng số ngày bình quân = 365/304 = 1,2 vòng.");
            replacements.put("{{vongQuay}}", "- Số vòng quay vốn bình quân:  1,2  Vòng/năm.");
            replacements.put("{{hm1}}", "+ Chính sách bán hàng: Bán buôn và bán lẻ cho các hộ kinh doanh, các đại lý trên địa bàn tỉnh và các vùng lân cận.");
            replacements.put("{{hm2}}", "+ Chính sách thu tiền hàng: Cho phép bên mua trả chậm tối đa không quá 90 ngày.");
            replacements.put("{{hm3}}", "* Xác điịnh thời gian bình quân cho một chu kỳ sản xuất:");
            replacements.put("{{tgpa}}", "duy trì hàn mức");
        } else {
            replacements.put("{{lvt}}", Optional.ofNullable(capitalizeWords(request.getLoaiVay())).orElse(""));
            replacements.put("{{slv}}", "từng lần");
            replacements.put("{{ms1t}}", "Số tiền cho vay:");
            replacements.put("{{ms1d}}", "Theo các điều khoản và điều kiện của Hợp đồng tín dụng này, bên A cho bên B vay khoản tiền bằng đồng Việt Nam. Số tiền vay là: " + request.getTienSo() + " đồng, (Bằng chữ: " + request.getTienChu() + " ).");
            replacements.put("{{ms2t}}", "Thời hạn cho vay: ");
            replacements.put("{{ms2d}}", "Thời hạn cho vay là: " + request.getHanMuc() + ", được tính từ ngày tiếp theo của ngày giải ngân đến ngày " + ngayKetThuc + " (trường hợp ngày cuối cùng của thời hạn vay là ngày lễ hoặc là ngày thứ 7, chủ nhật hàng tuần, thì ngày đến hạn chuyển sang ngày làm việc tiếp theo; nếu trường hợp bên B sử dụng chưa đủ một ngày, thì tính từ thời điểm nhận tiền vay và thời gian vay vốn được tính là 01 (một) ngày)");
            replacements.put("{{ms3}}", "Phương thức cho vay: Cho vay Từng lần.");
            replacements.put("{{ms4}}", "Mục đích sử dụng vốn vay: " + request.getMuchDichVay());
            replacements.put("{{ms5}}", "");
            replacements.put("{{ms6t}}", "");
            replacements.put("{{ms6d}}", "");
            replacements.put("{{tghm}}", "");
            replacements.put("{{vqhm}}", "");
            replacements.put("{{vongQuay}}", "");
            replacements.put("{{hm1}}", "");
            replacements.put("{{hm2}}", "");
            replacements.put("{{hm3}}", "");
            replacements.put("{{tgpa}}", "sử dụng vốn vay");
        }
        CreditContractTSBDRequest tsbdDto = request.getTsbdRequest();
        if (tsbdDto != null && Boolean.TRUE.equals(tsbdDto.getCheckTaiSanGanLienVoiDat())) {
            replacements.put("{{dienTichTS}}", Optional.ofNullable(tsbdDto.getDienTichTS()).orElse(""));
            replacements.put("{{ketCauXayDung}}", Optional.ofNullable(tsbdDto.getKetCauXayDung()).orElse(""));
            replacements.put("{{loaiNha}}", Optional.ofNullable(tsbdDto.getLoaiNha()).orElse(""));
        } else {
            replacements.put("{{dienTichTS}}", "0");
            replacements.put("{{ketCauXayDung}}", "0");
            replacements.put("{{loaiNha}}", "0");
        }
        CreditContractPAVVRequest pavvDto = request.getPavvRequest();
        if (pavvDto != null && Boolean.TRUE.equals(pavvDto.getCheckAddress())) {
            String address = "- Địa điểm thực hiện phương án: " + pavvDto.getAddress();
            replacements.put("{{ddpavv}}", address);
        } else {
            replacements.put("{{ddpavv}}", "");
        }
        if (pavvDto != null) {
            replacements.put("{{tenpavv}}", Optional.ofNullable(pavvDto.getName()).orElse(""));
            replacements.put("{{ldpavv}}", Optional.ofNullable(pavvDto.getReason()).orElse(""));
//            replacements.put("{{tongVon}}", Optional.ofNullable(pavvDto.getTongVon()).orElse(""));
//            replacements.put("{{tongVonLuuDong}}", Optional.ofNullable(pavvDto.getTongVonLuuDong()).orElse(""));
//            replacements.put("{{vonTuCo}}", Optional.ofNullable(pavvDto.getVonTuCo()).orElse(""));
//            replacements.put("{{vonKhac}}", Optional.ofNullable(pavvDto.getVonKhac()).orElse(""));
            //TÍNH TOÁN PHẦN TRĂM TỔNG TIỀN VAY VỐN
            double tongVonLuuDong = Optional.ofNullable(pavvDto.getTongVonLuuDong())
                    .filter(v -> !v.toString().isBlank()) // bỏ qua chuỗi rỗng
                    .map(v -> Double.parseDouble(v.toString().replace(".", "")))
                    .orElse(0.0);

            double vonTuCo = Optional.ofNullable(pavvDto.getVonTuCo())
                    .filter(v -> !v.toString().isBlank())
                    .map(v -> Double.parseDouble(v.toString().replace(".", "")))
                    .orElse(0.0);

            double vonKhac = Optional.ofNullable(pavvDto.getVonKhac())
                    .filter(v -> !v.toString().isBlank())
                    .map(v -> Double.parseDouble(v.toString().replace(".", "")))
                    .orElse(0.0);

            double tienSo = Optional.ofNullable(request.getTienSo())
                    .filter(v -> !v.toString().isBlank())
                    .map(v -> Double.parseDouble(v.toString().replace(".", "")))
                    .orElse(0.0);

            System.err.println("tienSo = " + tienSo);
            boolean isThoaThuan = request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)");
            Map<String, String> percents = calculatePercents(tongVonLuuDong, vonTuCo, vonKhac, tienSo, isThoaThuan);
            replacements.put("{{vonTuCoPercent}}", percents.get("vonTuCoPercent"));
            replacements.put("{{vonKhacPercent}}", percents.get("vonKhacPercent"));
            replacements.put("{{tienSoPercent}}", percents.get("tienSoPercent"));
            if (percents.containsKey("vonLuuDongMotVongQuay")) {
                replacements.put("{{vonLuuDongMotVongQuay}}", "- Vốn lưu động cần thiết cho một vòng quay: " + percents.get("vonLuuDongMotVongQuay") + " đồng");
            }
        }
        replacements.put("{{land_items}}", Optional.ofNullable(request.getLandItems()).orElse(""));
        replacements.put("{{thv}}", Optional.ofNullable(request.getThoiHanVay()).orElse(""));
        replacements.put("{{ncd}}", Optional.ofNullable(request.getNhaCoDinh()).orElse(""));
        replacements.put("{{tsbds}}", Optional.ofNullable(request.getTongTaiSanBD()).orElse(""));
        replacements.put("{{tsbdc}}", Optional.ofNullable(request.getTongTaiSanBDChu()).orElse(""));
//        replacements.put("{{phuong}}", extractPhuong(request.getDiaChiThuongTruKhachHang()));
        replacements.put("{{day}}", String.format("%02d", date.getDayOfMonth()));
        replacements.put("{{month}}", String.format("%02d", date.getMonthValue()));
        replacements.put("{{year}}", String.valueOf(date.getYear()));
        replacements.put("{{dayTC}}", String.format("%02d", dateTC.getDayOfMonth()));
        replacements.put("{{monthTC}}", String.format("%02d", dateTC.getMonthValue()));
        replacements.put("{{yearTC}}", String.valueOf(dateTC.getYear()));
        replacements.put("{{dayBD}}", String.format("%02d", dateBD.getDayOfMonth()));
        replacements.put("{{monthBD}}", String.format("%02d", dateBD.getMonthValue()));
        replacements.put("{{yearBD}}", String.valueOf(dateBD.getYear()));
        replacements.put("{{canBoTD}}", "VŨ XUÂN LONG");
        replacements.put("{{sdtCanBoTD}}", "0987858237");
        replacements.put("{{canBoTDVT}}", capitalizeWords("VŨ XUÂN LONG"));
        String regex = "\\d+(,\\d+)?";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(request.getLaiSuat());
        if (matcher.find()) {
            String result = matcher.group();
            replacements.put("{{lss}}", result);
            System.out.println("Kết quả: " + result);
        } else {
            System.out.println("Không tìm thấy số.");
        }
        //TẠO END DATE
        String thv = request.getThoiHanVay();
        LocalDate endDate = date; // mặc định là ngày bắt đầu

        if (thv != null && !thv.isEmpty()) {
            try {
                int years = Integer.parseInt(thv.trim()); // parse số năm
                endDate = date.plusYears(years);
            } catch (NumberFormatException e) {
                System.err.println("Không parse được thv: " + thv);
            }
        }
// Thêm placeholder mới
        replacements.put("{{endDate}}", endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        //CUỐI TẠO END DATE
        if (request.getCheckNguoiDungTenBiaDo2()) {
            System.err.println("===============DUNG TEN BI DO 2 ========================");
            replacements.put("{{ntbdd1}}", request.getGioiTinhDungTenBiaDo2() + ": " + request.getDungTenBiaDo2() + "; Sinh năm: " + request.getNamSinhDungTenBiaDo2() + ".");
            replacements.put("{{ntbdd2}}", "CCCD số: " + request.getCccdDungTenBiaDo2() + "; Ngày cấp: " + request.getNgayCapCCCDDungTenBiaDo2() + "; Nơi cấp: " + request.getNoiCapCCCDDungTenBiaDo2() + ".");
            replacements.put("{{ntbdd3}}", "Địa chỉ thường trú: " + request.getDiaChiThuongTruDungTenBiaDo2() + ".");
            replacements.put("{{ntbdd4}}", "3.6. Họ và tên đầy đủ đối với cá nhân/tên đầy đủ đối với tổ chức: (viết chữ IN HOA)");
            replacements.put("{{ntbdd5}}", "Năm sinh: " + request.getNamSinhDungTenBiaDo2());
            replacements.put("{{ntbdd6}}", "3.7. Địa chỉ thường trú:  " + request.getDiaChiThuongTruDungTenBiaDo2());
            replacements.put("{{ntbdd7}}", "3.8. Giấy tờ xác định tư cách pháp lý: ");
            replacements.put("{{ntbdd8}}", "☑ Chứng minh nhân dân/Căn cước công dân/Chứng minh quân đội");
            replacements.put("{{ntbdd9}}", "□ Hộ chiếu        □ Thẻ thường trú        □ Mã số thuế");
            replacements.put("{{ntbdd10}}", "CCCD số: " + request.getCccdDungTenBiaDo2() + "; Ngày cấp: " + request.getNgayCapCCCDDungTenBiaDo2() + "; Nơi cấp: " + request.getNoiCapCCCDDungTenBiaDo2() + ";");
            replacements.put("{{ntbdd11}}", "3.9. Thuộc đối tượng không phải nộp phí đăng ký □");
            replacements.put("{{ntbdd12}}", "3.10. Số điện thoại (nếu có):…..Fax (nếu có):……Thư điện tử (nếu có):………………..");
            System.err.println("===============END DUNG TEN BI DO 2 ========================");
        } else {
            replacements.put("{{ntbdd1}}", "");
            replacements.put("{{ntbdd2}}", "");
            replacements.put("{{ntbdd3}}", "");
            replacements.put("{{ntbdd4}}", "");
            replacements.put("{{ntbdd5}}", "");
            replacements.put("{{ntbdd6}}", "");
            replacements.put("{{ntbdd7}}", "");
            replacements.put("{{ntbdd8}}", "");
            replacements.put("{{ntbdd9}}", "");
            replacements.put("{{ntbdd10}}", "");
            replacements.put("{{ntbdd11}}", "");
            replacements.put("{{ntbdd12}}", "");
        }
        if (request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN")) {
            replacements.put("{{loaivay}}", "Cho vay ngắn hạn");
        } else if (request.getLoaiVay().equalsIgnoreCase("TRUNG HẠN")) {
            replacements.put("{{loaivay}}", "Cho vay trung hạn");
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
            replacements.put("{{dcpgd}}", "Trụ sở tại: Số 178, TDP Ninh Chấp 5, phường Chu Văn An, thành phố Hải Phòng.\n" +
                    "Giấy phép đăng ký kinh doanh: 0800001806; Điện thoại: 02203.882.700\n");
            replacements.put("{{ndd}}", "bà: PHÙNG THỊ LOAN Chức vụ: Giám Đốc điều hành\n" +
                    "CCCD số: 030182016564; Cấp ngày: 22/12/2021. Nơi cấp: Cục cảnh sát quản lý hành chính về trật tự xã hội.");
            replacements.put("{{ndd1}}", "Bà: " + capitalizeWords("PHÙNG THỊ LOAN") + " - Chức vụ: Giám Đốc điều hành.");
            replacements.put("{{pgd}}", "");
            replacements.put("{{pgdvt}}", "");
            replacements.put("{{phuong}}", "Chu Văn An");
            replacements.put("{{chuTichPhuong}}", "..........................");
            replacements.put("{{nguoiTiepNhanHoSo}}", "Phạm Thị Thơm");
            replacements.put("{{diaChiLienHe}}", "Số 178 Ninh Chấp 5, phường Chu Văn An,");
//            replacements.put("{{canBoTD}}", "VŨ XUÂN LONG");
//            replacements.put("{{canBoTDVT}}", capitalizeWords("VŨ XUÂN LONG"));
//            replacements.put("{{sdtCanBoTD}}", "0987858237");
            replacements.put("{{gmail}}", "thaihocqtd@gmail.com");
            replacements.put("{{nddpl}}", "Giám Đốc");
            replacements.put("{{gdpgd}}", "Phùng Thị Loan");
        } else if (request.getNguoiDaiDien().equalsIgnoreCase("pgd")) {
            replacements.put("{{pgd}}", " - PHÒNG GIAO DỊCH AN LẠC");
            replacements.put("{{pgdvt}}", capitalizeWords(" - PHÒNG GIAO DỊCH AN LẠC"));
            replacements.put("{{dcpgd}}", "Địa chỉ: TDP Lạc Đạo, phường Lê Đại Hành, thành phố Hải Phòng. " +
                    "Giấy phép đăng ký kinh doanh: 0800001806; Điện thoại: 0220.3596.266");
            replacements.put("{{ndd}}", "ông: DƯƠNG QUANG TUẤN Chức vụ: Giám Đốc - PGD An Lạc.\n" +
                    "CCCD số: 030087002460;\n" +
                    "(Theo văn bản ủy quyền số: 01/2026/UQ-TN Ngày 05 tháng 01 năm 2026)");
            replacements.put("{{ndd1}}", "Ông: " + capitalizeWords("DƯƠNG QUANG TUẤN") + " - Chức vụ: Giám Đốc - PGD An Lạc.");
            replacements.put("{{phuong}}", "Lê Đại Hành");
            replacements.put("{{chuTichPhuong}}", "Phương Quốc Luyện");
            replacements.put("{{nguoiTiepNhanHoSo}}", "Nguyễn Văn Chiến");
            replacements.put("{{diaChiLienHe}}", "TDP Lạc Đạo, phường Lê Đại Hành,");
//            replacements.put("{{canBoTD}}", "DƯƠNG QUANG TUẤN");
//            replacements.put("{{canBoTDVT}}", capitalizeWords("DƯƠNG QUANG TUẤN"));
//            replacements.put("{{sdtCanBoTD}}", "0906676333");
            replacements.put("{{gmail}}", "pgdanlac888@gmail.com");
            replacements.put("{{nddpl}}", "Giám Đốc PGD");
            replacements.put("{{gdpgd}}", "Dương Quang Tuấn");
        }
        if (request.getCheckHopDongBaoLanh()) {
            String doanVanBan = "Bên B dùng tài sản này để đảm bảo việc thanh toán được kịp thời, đầy đủ và thực hiện một cách " +
                    "trọn vẹn khi đến hạn các nghĩa vụ trả nợ đối với hợp đồng cho vay số:" + request.getSoHopDongTD() + "của "
                    + request.getGtkh().toLowerCase() + " " + capitalizeWords(request.getTenKhachHang()) + " " + request.getGtnt().toLowerCase() + " " +
                    capitalizeWords(request.getTenNguoiThan()) + " hoặc các hợp đồng cho vay khác có tham chiếu từ hợp đồng thế chấp này";
            String doanVanBan2 = "theo hợp đồng cho vay số: " + request.getSoHopDongTD() + " và hợp đồng cho vay khác (nếu có) mà tài sản thế chấp này làm bảo đảm";
            replacements.put("{{tstc}}", doanVanBan);
            replacements.put("{{dvb}}", doanVanBan2);
        } else {
            String doanVanBan = "Để đảm bảo việc thanh toán được kịp thời, đầy đủ và thực hiện một cách trọn vẹn khi đến hạn các nghĩa vụ trả nợ đang " +
                    "tồn tại hoặc sẽ phát sinh trong tương lai của Bên B cho Bên A theo các " +
                    "Hợp đồng cho vay và/hoặc các Hợp đồng khác có tham chiếu từ Hợp đồng này";
            replacements.put("{{tstc}}", doanVanBan);
            replacements.put("{{dvb}}", "của Bên B");
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
            ) {
                targets.add(para);
            }
        }
// Bước 2: thay thế placeholder bằng bảng
        for (XWPFParagraph para : targets) {
            String text = para.getText();

            if (text.contains("{{TABLE_PLACEHOLDER}}")) {
                insertTableAtPlaceholder(doc, para, request.getTableRequest(), true, replacements, request);
            }
            if (text.contains("{{TABLE_TN_PLACEHOLDER}}")) {
                insertTableAtPlaceholder(doc, para, request.getThuNhapDuKienTable(), false, replacements, request);
            }
            if (text.contains("{{TABLE1_PLACEHOLDER}}")) {
                insertTableAtPlaceholder(doc, para, request.getTable1(), false, replacements, request);
            }
            if (text.contains("{{TABLE2_PLACEHOLDER}}")) {
                insertTableAtPlaceholder(doc, para, request.getTable2(), false, replacements, request);
            }
            if (text.contains("{{TABLE3_PLACEHOLDER}}")) {
                insertTableAtPlaceholder(doc, para, request.getTable3(), false, replacements, request);
            }
            if (text.contains("{{TABLE_HM_PLACEHOLDER}}")) {
                if (request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)")) {
                    insertTableAtPlaceholder(doc, para, request.getHanMucTable(), false, replacements, request);
                } else {
                    // Nếu không phải ngắn hạn thỏa thuận thì xóa placeholder để tránh in header rỗng
                    int paraPos = doc.getPosOfParagraph(para);
                    if (paraPos >= 0) {
                        doc.removeBodyElement(paraPos);
                    }
                }
            }
            if (text.contains("{{TABLE_CP_PLACEHOLDER}}")) {
                insertTableAtPlaceholder(doc, para, request.getChiPhiTable(), false, replacements, request);
            }
        }

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


    private Map<String, String> calculatePercents(double tongVonLuuDong, double vonTuCo, double vonKhac, double tienSo, boolean isThoaThuan) {
        // Nếu là thỏa thuận thì base = tongVonLuuDong, ngược lại base = tongVonLuuDong / 1.2
        double baseTotal = isThoaThuan ? tongVonLuuDong / 1.2 : tongVonLuuDong;

        Map<String, String> result = new HashMap<>();

        // Format phần trăm với dấu phẩy (Locale GERMAN dùng dấu phẩy cho thập phân)
        NumberFormat percentFormat = NumberFormat.getNumberInstance(Locale.GERMAN);
        percentFormat.setMaximumFractionDigits(1);

        result.put("vonTuCoPercent", baseTotal > 0 ? percentFormat.format((vonTuCo / baseTotal) * 100) + "%" : "0%");
        result.put("vonKhacPercent", baseTotal > 0 ? percentFormat.format((vonKhac / baseTotal) * 100) + "%" : "0%");
        result.put("tienSoPercent", baseTotal > 0 ? percentFormat.format((tienSo / baseTotal) * 100) + "%" : "0%");

        // Nếu không phải thỏa thuận thì thêm placeholder vonLuuDongMotVongQuay
        if (isThoaThuan) {
            // Format tiền theo chuẩn Việt Nam (dấu chấm ngăn cách hàng nghìn)
            NumberFormat moneyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
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

        if (tableRequest == null) {
            return;
        }

        if (checkDrawTable && !tableRequest.isDrawTable()) {
            return;
        }

        XmlCursor cursor = para.getCTP().newCursor();

        // INSERT TABLE TRƯỚC
        XWPFTable table = doc.insertNewTbl(cursor);

        // Sau đó mới xóa paragraph placeholder
        int paraPos = doc.getPosOfParagraph(para);
        if (paraPos >= 0) {
            doc.removeBodyElement(paraPos);
        }

        if (table.getNumberOfRows() > 0) {
            table.removeRow(0);
        }

        String tableType = tableRequest.getTableType();

        if ("hanMuc".equalsIgnoreCase(tableType)) {
            fillHanMucTable(table, tableRequest);
        } else if ("chiPhi".equalsIgnoreCase(tableType)) {
            fillChiPhiTable(table, tableRequest, replacements, request);
        } else if ("thuNhapDuKien".equalsIgnoreCase(tableType)) {
            fillThuNhapTable(table, tableRequest, replacements);
        } else {
            fillGenericTable(table, tableRequest);
        }
    }


    private void fillGenericTable(XWPFTable table, TableRequest tableRequest) {
        if (table == null || tableRequest == null) return;
        if (!tableRequest.isDrawTable()) return;

        // Xác định số cột: nếu có header thì lấy header.size(), nếu không thì lấy row dài nhất
        int colCount = tableRequest.getHeaders() != null ? tableRequest.getHeaders().size() : 0;
        for (List<String> rowData : tableRequest.getRows()) {
            if (rowData.size() > colCount) colCount = rowData.size();
        }

        // Header nếu có
        if (tableRequest.getHeaders() != null && !tableRequest.getHeaders().isEmpty()) {
            XWPFTableRow headerRow = table.createRow();
            while (headerRow.getTableCells().size() < colCount) {
                XWPFTableCell cell = headerRow.addNewTableCell();
                cell.addParagraph(); // đảm bảo có <p>
            }
            for (int c = 0; c < tableRequest.getHeaders().size(); c++) {
                XWPFTableCell cell = headerRow.getCell(c);
                XWPFParagraph para = cell.getParagraphs().isEmpty() ? cell.addParagraph() : cell.getParagraphs().get(0);
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
                cell.addParagraph(); // đảm bảo có <p>
            }
            for (int c = 0; c < colCount; c++) {
                String cellValue = c < rowData.size() ? rowData.get(c) : "";
                XWPFTableCell cell = row.getCell(c);
                XWPFParagraph para = cell.getParagraphs().isEmpty() ? cell.addParagraph() : cell.getParagraphs().get(0);
                XWPFRun run = para.createRun();
                run.setFontFamily("Times New Roman");
                run.setFontSize(13);
                run.setText(cellValue != null ? cellValue : "");
            }
            applyBordersToRow(row);
            // Sau khi fill xong
            for (XWPFTableRow row3 : table.getRows()) {
                for (XWPFTableCell cell : row3.getTableCells()) {
                    System.out.println("Cell paragraphs Generic: " + cell.getParagraphs().size());
                }
            }

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
                    replaced = replaced.replace(entry.getKey(), entry.getValue());
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
                setCellText(headerRow.getCell(c), tableRequest.getHeaders().get(c), true, false);
            }
            applyBordersToRow(headerRow);
        }

        // ===== Data rows =====
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        for (List<String> rowData : tableRequest.getRows()) {
            XWPFTableRow row = table.createRow();
            ensureCells(row, colCount);
            ensureParagraphsInRow(row);
            for (int c = 0; c < colCount; c++) {
                String cellValue = c < rowData.size() ? rowData.get(c) : "";

                // Format số cho các cột số lượng, đơn giá, thành tiền
                if (c == 2 || c == 3 || c == 4) {
                    try {
                        if (cellValue != null && !cellValue.isBlank()) {
                            long number = Long.parseLong(cellValue.replace(".", "").trim());
                            cellValue = nf.format(number);
                        }
                    } catch (NumberFormatException e) {
                        // giữ nguyên nếu không parse được
                    }
                }

                setCellText(row.getCell(c), cellValue, false, false);
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
        lastRow.set(4, nf.format(tongThuNhap));

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
                        setCellText(cell, text, false, true);
                    }
                }

                // Xử lý merge
                XWPFTableCell baseCell = mergedRow.getCell(startCol);
                if (baseCell == null) continue;
                setCellText(baseCell, merge.getMergedValue() != null ? merge.getMergedValue() : "", false, true);
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
                setCellText(headerRow.getCell(i), tableRequest.getHeaders().get(i), true, false);
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
                setCellText(row.getCell(i), value, false, false);
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
                setCellText(baseCell, merge.getMergedValue() != null ? merge.getMergedValue() : "", false, true);
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

    // ======= Hàm chính: fillChiPhiTable =======
    private void fillChiPhiTable(XWPFTable table, TableRequest tableRequest, Map<String, String> replacements, ContractRequest request) {
        if (table == null || tableRequest == null || !tableRequest.isDrawTable()) return;

        int colCount = tableRequest.getHeaders() != null ? tableRequest.getHeaders().size() : 0;

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

        // ===== Header =====
        boolean hasHeader = tableRequest.getHeaders() != null && !tableRequest.getHeaders().isEmpty();
        if (hasHeader) {
            XWPFTableRow headerRow = table.createRow();
            ensureCells(headerRow, colCount);
            ensureParagraphsInRow(headerRow);
            for (int c = 0; c < colCount; c++) {
                setCellText(headerRow.getCell(c), tableRequest.getHeaders().get(c), true, false);
            }
            applyBordersToRow(headerRow);
        }

        // ===== Data rows =====
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN")); // formatter tiền tệ VN
        for (List<String> rowData : tableRequest.getRows()) {
            XWPFTableRow row = table.createRow();
            ensureCells(row, colCount);
            ensureParagraphsInRow(row);
            for (int c = 0; c < colCount; c++) {
                String cellValue = c < rowData.size() ? rowData.get(c) : "";

                // Format các cột số: Số lượng (3), Đơn giá (4), Thành tiền (5)
                if (c == 3 || c == 4 || c == 5) {
                    try {
                        if (cellValue != null && !cellValue.isBlank()) {
                            long number = Long.parseLong(cellValue.replace(".", "").trim());
                            cellValue = nf.format(number);
                        }
                    } catch (NumberFormatException e) {
                        // giữ nguyên nếu không parse được
                    }
                }

                setCellText(row.getCell(c), cellValue, false, false);
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
                        setCellText(cell, text, false, true);
                    }
                }

                // Xử lý merge
                XWPFTableCell baseCell = mergedRow.getCell(startCol);
                if (baseCell == null) continue;
                setCellText(baseCell, merge.getMergedValue() != null ? merge.getMergedValue() : "", false, true);
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

        // ===== Tính {{tongNCV}} =====
        long tongChiPhi = 0;
        long chiPhiGianTiep = 0;

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

        long tongNCV = tongChiPhi - chiPhiGianTiep;
        String tongNCVFormatted = nf.format(tongNCV);
        replacements.put("{{tongNCV}}", tongNCVFormatted);
        // Lấy tiền số từ request
        long tienSo = 0;
        try {
            tienSo = Long.parseLong(Optional.ofNullable(request.getTienSo()).orElse("0").replace(".", "").trim());
            System.err.println("TIEN SO ====> " + tienSo);
        } catch (Exception e) {
            tienSo = 0;
        }

        // Tính vonTuCo
        long vonTuCo = tongNCV - tienSo;
        System.err.println("VON TU CO ====> " + vonTuCo);
        // Tính phần trăm
        double phanTramVTC = tongNCV > 0 ? (double) vonTuCo / tongNCV * 100 : 0;
        double phanTramVV = tongNCV > 0 ? (double) tienSo / tongNCV * 100 : 0;

        // Format và gán vào replacements
//        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        replacements.put("{{vonTuCo}}", nf.format(vonTuCo));
        replacements.put("{{phanTramVTC}}", String.format("%.2f", phanTramVTC) + "%");
        replacements.put("{{phanTramVV}}", String.format("%.2f", phanTramVV) + "%");
    }

    // Hàm phụ để parse số an toàn
    private long parseLongSafe(String value) {
        try {
            return Long.parseLong(value.replace(".", "").trim());
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

    // Set text an toàn: luôn tạo paragraph mới, font Times New Roman
    private void setCellText(XWPFTableCell cell, String text, boolean isHeader, boolean isBold) {
        while (cell.getParagraphs().size() > 0) {
            cell.removeParagraph(0);
        }
        XWPFParagraph para = cell.addParagraph();
        para.setAlignment(isHeader ? ParagraphAlignment.CENTER : ParagraphAlignment.LEFT);
        XWPFRun run = para.createRun();
        run.setFontFamily("Times New Roman");
        run.setFontSize(12);
        run.setBold(isHeader || isBold);

        if (text != null && !text.isEmpty()) {
            run.setText(text);
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
        }
    }

}
