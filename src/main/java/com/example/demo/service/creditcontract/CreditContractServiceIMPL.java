package com.example.demo.service.creditcontract;

import com.example.demo.dto.request.ContractRequest;
import com.example.demo.dto.request.TableRequest;
import com.example.demo.mapper.ContractMapper;
import com.example.demo.model.CreditContractEntity;

import java.text.NumberFormat;
import java.util.Locale;

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
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "PhuLucHopDong.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "BienBanKiemTraSauKhiChoVay.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "BienBanXetDuyetChoVay.docx"));
        fileUrls.add(generateContractFile(request, date, dateTC, dateBD, user, "BienBanXacDinhGiaTriTaiSanBaoDam.docx"));

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
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "HopDongTinDung.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "HopDongTheChap.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "PhieuBaoDamQSDD.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "GiayDeNghiVayVon.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "DanhMucHoSoChoVay.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "PhuLucHopDong.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BienBanKiemTraSauKhiChoVay.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BienBanXetDuyetChoVay.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BienBanXacDinhGiaTriTaiSanBaoDam.docx"));

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
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "PhuLucHopDong.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BienBanKiemTraSauKhiChoVay.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BienBanXetDuyetChoVay.docx"));
        fileUrls.add(generateContractFileExport(request, date, dateTC, dateBD, user, "BienBanXacDinhGiaTriTaiSanBaoDam.docx"));

        creditContractRepository.save(entity);
        return fileUrls;
    }

    // 👉 Hàm generate file (preview)
    private String generateContractFile(ContractRequest request, LocalDate date, LocalDate dateTC, LocalDate dateBD, User user, String templateName) throws IOException {
        try (InputStream is = new ClassPathResource("templates/" + templateName).getInputStream();
             XWPFDocument doc = new XWPFDocument(is)) {

            replacePlaceholders(doc, request, date, dateTC, dateBD);

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
                                             @NotNull LocalDate dateTC,
                                             @NotNull LocalDate dateBD,
                                             @NotNull User user,
                                             @NotNull String templateName) throws IOException {
        try (InputStream is = new ClassPathResource("templates/" + templateName).getInputStream();
             XWPFDocument doc = new XWPFDocument(is)) {

            replacePlaceholders(doc, request, date, dateTC, dateBD);

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


    private void replacePlaceholders(XWPFDocument doc, ContractRequest request, LocalDate date, LocalDate dateTC, LocalDate dateBD) {
        System.err.println("request --> " + request);
        long gtqsdd = Long.parseLong(Optional.ofNullable(request.getGiaTriQuyenSuDungDat()).orElse(String.valueOf(0L)));
// Định dạng theo locale Việt Nam
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedGtqsdd = nf.format(gtqsdd);
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
        replacements.put("{{sbbxdcv}}", Optional.ofNullable(request.getSoBBXetDuyetChoVay()).orElse(""));
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
        if(request.getLoaiVay().equalsIgnoreCase("NGẮN HẠN (Thỏa thuận)")){
            replacements.put("{{lvt}}", Optional.ofNullable("Ngắn hạn").orElse(""));
            replacements.put("{{slv}}", "hạn mức");
            System.err.println("get::"+replacements.get("{{slv}}"));
            replacements.put("{{ms1t}}", "Phương thức cho vay: Cho vay theo hạn mức");
            replacements.put("{{ms1d}}", "");
            replacements.put("{{ms2t}}", "Hạn mức cho vay:");
            replacements.put("{{ms2d}}", "Bên A cam kết cho bên B vay các khoản cấp tín dụng bằng đồng Việt Nam với hạn mức cho vay là: "+request.getTienSo()+" đồng, (Bằng chữ: "+request.getTienChu()+ " )");
            replacements.put("{{ms3}}", "Mục đích sử dụng tiền vay: "+request.getMuchDichVay());
            replacements.put("{{ms4}}", "Thời hạn duy trì hạn mức: "+request.getHanMuc()+ ", kể từ ngày ký thỏa thuận Hợp đồng tín dụng này. Trong khoảng thời gian này Bên B được đề nghị Bên A cấp tín dụng phù hợp với mục đích sử dụng vốn và có thể đề nghị giải ngân một lần hoặc nhiều lần trong hạn mức nêu tại Hợp đồng tín dụng này. Hết thời hạn duy trì hạn mức hợp đồng này, bên A không có nghĩa vụ giải ngân bất kỳ khoản vay nợ nào");
            replacements.put("{{ms5}}", "5. Một năm ít nhất một lần bên A có trách nhiệm xem xét, xác định lại hạn mức cho vay tối đa và thời gian duy trì hạn mức Hợp đồng này.");
            replacements.put("{{ms6t}}", "6. Thời hạn cho vay: Từng khoản cấp tín dụng được xác định cụ thể trên từng giấy nhận nợ, mỗi giấy nhận nợ có thời gian cho vay khác nhau và được Bên A xác định vào chu kỳ sản xuất kinh doanh, khả năng trả nợ của Bên B và không vượt quá 10 tháng hoặc không vượt quá một thời hạn khác do Bên A xác định trong từng thời kỳ.");
            replacements.put("{{ms6d}}", "Thời hạn cho vay của từng khoản cấp tín dụng cụ thể được tính từ ngày tiếp theo của ngày giải ngân cho đến thời điểm trả hết toàn bộ tiền gốc, lãi tiền vay và các chi phí phát sinh liên quan. Trong trường hợp Bên B sử dụng tiền vay chưa đủ một ngày, thì tính từ thời điểm nhận tiền vay và thời gian vay vốn được tính là 01 (một) ngày và trường hợp ngày cuối cùng của thời hạn vay là ngày lễ hoặc thứ 7, chủ nhật hàng tuần, thì ngày đến hạn chuyển sang ngày làm việc tiếp theo.");
        } else {
            replacements.put("{{lvt}}", Optional.ofNullable(capitalizeWords(request.getLoaiVay())).orElse(""));
            replacements.put("{{slv}}", "từng lần");
            replacements.put("{{ms1t}}", "Số tiền cho vay:");
            replacements.put("{{ms1d}}", "Theo các điều khoản và điều kiện của Hợp đồng tín dụng này, bên A cho bên B vay khoản tiền bằng đồng Việt Nam. Số tiền vay là: "+request.getTienSo()+" đồng, (Bằng chữ: "+request.getTienChu()+ " ).");
            replacements.put("{{ms2t}}", "Thời hạn cho vay: ");
            replacements.put("{{ms2d}}", "Thời hạn cho vay là: "+request.getHanMuc()+ ", được tính từ ngày tiếp theo của ngày giải ngân đến ngày "+request.getNgayKetThucKyHanVay()+" (trường hợp ngày cuối cùng của thời hạn vay là ngày lễ hoặc là ngày thứ 7, chủ nhật hàng tuần, thì ngày đến hạn chuyển sang ngày làm việc tiếp theo; nếu trường hợp bên B sử dụng chưa đủ một ngày, thì tính từ thời điểm nhận tiền vay và thời gian vay vốn được tính là 01 (một) ngày)");
            replacements.put("{{ms3}}", "Phương thức cho vay: Cho vay Từng lần.");
            replacements.put("{{ms4}}", "Mục đích sử dụng vốn vay: "+request.getMuchDichVay());
            replacements.put("{{ms5}}", "");
            replacements.put("{{ms6t}}", "");
            replacements.put("{{ms6d}}", "");
        }
        replacements.put("{{land_items}}", Optional.ofNullable(request.getLandItems()).orElse(""));
        replacements.put("{{thv}}", Optional.ofNullable(request.getThoiHanVay()).orElse(""));
        replacements.put("{{ncd}}", Optional.ofNullable(request.getNhaCoDinh()).orElse(""));
        replacements.put("{{tsbds}}", Optional.ofNullable(request.getTongTaiSanBD()).orElse(""));
        replacements.put("{{tsbdc}}", Optional.ofNullable(request.getTongTaiSanBDChu()).orElse(""));
        replacements.put("{{phuong}}", extractPhuong(request.getDiaChiThuongTruKhachHang()));
        replacements.put("{{day}}", String.format("%02d", date.getDayOfMonth()));
        replacements.put("{{month}}", String.format("%02d", date.getMonthValue()));
        replacements.put("{{year}}", String.valueOf(date.getYear()));
        replacements.put("{{dayTC}}", String.format("%02d", dateTC.getDayOfMonth()));
        replacements.put("{{monthTC}}", String.format("%02d", dateTC.getMonthValue()));
        replacements.put("{{yearTC}}", String.valueOf(dateTC.getYear()));
        replacements.put("{{dayBD}}", String.format("%02d", dateBD.getDayOfMonth()));
        replacements.put("{{monthBD}}", String.format("%02d", dateBD.getMonthValue()));
        replacements.put("{{yearBD}}", String.valueOf(dateBD.getYear()));
        // Thêm placeholder mới dựa vào biến checkNguoiDungTenBiaDo2
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
            replacements.put("{{ntbdd1}}", request.getGioiTinhDungTenBiaDo2()+": "+request.getDungTenBiaDo2()+"; Sinh ngày: "+request.getNamSinhDungTenBiaDo2()+".");
            replacements.put("{{ntbdd2}}", "CCCD số: "+request.getCccdDungTenBiaDo2()+"; Ngày cấp: "+request.getNgayCapCCCDDungTenBiaDo2()+"; Nơi cấp: "+request.getNoiCapCCCDDungTenBiaDo2()+".");
            replacements.put("{{ntbdd3}}", "Địa chỉ thường trú: "+request.getDiaChiThuongTruDungTenBiaDo2()+".");
        } else {
            replacements.put("{{ntbdd1}}", "");
            replacements.put("{{ntbdd2}}", "");
            replacements.put("{{ntbdd3}}", "");
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
                    "trọn vẹn khi đến hạn các nghĩa vụ trả nợ đối với hợp đồng cho vay số:" + request.getSoHopDongTD() + "của "
                    + request.getGtkh().toLowerCase() + " " + capitalizeWords(request.getTenKhachHang()) + " " + request.getGtnt().toLowerCase() + " " +
                    capitalizeWords(request.getTenNguoiThan()) + " hoặc các hợp đồng cho vay khác có tham chiếu từ hợp đồng thế chấp này";
            String doanVanBan2 = "theo hợp đồng cho vay số: " +request.getSoHopDongTD()+ " và hợp đồng cho vay khác (nếu có) mà tài sản thế chấp này làm bảo đảm";
            replacements.put("{{tstc}}", doanVanBan);
            replacements.put("{{dvb}}", doanVanBan2);
        } else {
            String doanVanBan = "Để đảm bảo việc thanh toán được kịp thời, đầy đủ và thực hiện một cách trọn vẹn khi đến hạn các nghĩa vụ trả nợ đang " +
                    "tồn tại hoặc sẽ phát sinh trong tương lai của Bên B cho Bên A theo các " +
                    "Hợp đồng cho vay và/hoặc các Hợp đồng khác có tham chiếu từ Hợp đồng này";
            replacements.put("{{tstc}}", doanVanBan);
            replacements.put("{{dvb}}", "của Bên B");
        }
// Tìm paragraph có placeholder
        for (XWPFParagraph para : new ArrayList<>(doc.getParagraphs())) {
            String text = para.getText();
            if (text != null) {
                if (text.contains("{{TABLE_PLACEHOLDER}}")) {
                    // bảng tuỳ chọn, chỉ tạo nếu drawTable = true
                    insertTableAtPlaceholder(doc, para, request.getTableRequest(), true);
                }
                if (text.contains("{{TABLE1_PLACEHOLDER}}")) {
                    // bảng bắt buộc, luôn tạo
                    insertTableAtPlaceholder(doc, para, request.getTable1(), false);
                }
                if (text.contains("{{TABLE2_PLACEHOLDER}}")) {
                    insertTableAtPlaceholder(doc, para, request.getTable2(), false);
                }
                if (text.contains("{{TABLE3_PLACEHOLDER}}")) {
                    insertTableAtPlaceholder(doc, para, request.getTable3(), false);
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

    //    private void copyStyle(XWPFRun source, XWPFRun target) {
//        if (source.getCTR() != null) {
//            target.getCTR().setRPr(source.getCTR().getRPr());
//            // giữ nguyên định dạng, kể cả superscript/subscript
//        }
//    }
    private void insertTableAtPlaceholder(XWPFDocument doc, XWPFParagraph para, TableRequest tableRequest,
                                          boolean checkDrawTable) {
        // Xóa nội dung placeholder
        for (int i = para.getRuns().size() - 1; i >= 0; i--) {
            para.removeRun(i);
        }

        if (tableRequest != null) {
            if (!checkDrawTable || tableRequest.isDrawTable()) {
                XmlCursor cursor = para.getCTP().newCursor();
                XWPFTable table = doc.insertNewTbl(cursor);
                if (table != null) {
                    fillInsertedTable(table, tableRequest, checkDrawTable);
                }
            }
        }

        // Xóa paragraph placeholder để không còn dư
        IBody body = para.getBody();
        if (body instanceof XWPFDocument d) {
            int pos = d.getPosOfParagraph(para);
            if (pos >= 0) d.removeBodyElement(pos);
        } else if (body instanceof XWPFTableCell cell) {
            int idx = cell.getParagraphs().indexOf(para);
            if (idx >= 0) cell.removeParagraph(idx);
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

        // Ghép toàn bộ text của paragraph
        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null) fullText.append(text);
        }
        String paragraphText = fullText.toString();
        if (paragraphText.isEmpty()) return;

        // Thay thế tất cả placeholder trong đoạn văn
        String replacedText = paragraphText;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            replacedText = replacedText.replace(entry.getKey(), entry.getValue());
        }

        // Nếu sau khi thay thế mà chỉ còn trống hoặc toàn khoảng trắng → xóa paragraph
        if (replacedText.trim().isEmpty()) {
            IBody body = paragraph.getBody();
            if (body instanceof XWPFDocument d) {
                int pos = d.getPosOfParagraph(paragraph);
                if (pos >= 0) d.removeBodyElement(pos);
            } else if (body instanceof XWPFTableCell cell) {
                int idx = cell.getParagraphs().indexOf(paragraph);
                if (idx >= 0) cell.removeParagraph(idx);
            }
            return;
        }

        // Nếu không có thay đổi thì bỏ qua
        if (replacedText.equals(paragraphText)) return;

        // Xóa nội dung cũ trong các run nhưng giữ style
        for (XWPFRun run : runs) {
            run.setText("", 0);
        }

        // Ghi lại text đã thay thế vào run đầu tiên
        XWPFRun baseRun = runs.get(0);
        baseRun.setText(replacedText, 0);
    }


    private void fillInsertedTable(XWPFTable table, TableRequest tableRequest, boolean checkDrawTable) {
        if (table == null || tableRequest == null) return;
        if (checkDrawTable && !tableRequest.isDrawTable()) return;

        int dataRowCount = tableRequest.getRows().size();
        int colCount = dataRowCount > 0 ? tableRequest.getRows().get(0).size() : 0;

        table.setTableAlignment(TableRowAlign.CENTER);
        table.setWidth("8000");

        // Tạo đủ số hàng
        while (table.getNumberOfRows() < dataRowCount) {
            XWPFTableRow newRow = table.createRow();
            while (newRow.getTableCells().size() < colCount) {
                newRow.addNewTableCell();
            }
        }

        for (int r = 0; r < dataRowCount; r++) {
            XWPFTableRow row = table.getRow(r);
            while (row.getTableCells().size() < colCount) {
                row.addNewTableCell();
            }

            List<String> rowData = tableRequest.getRows().get(r);
            for (int c = 0; c < colCount; c++) {
                String cellValue = c < rowData.size() ? rowData.get(c) : "";
                XWPFTableCell cell = row.getCell(c);
                if (cell == null) cell = row.addNewTableCell();

                XWPFParagraph para = cell.getParagraphs().get(0);
                para.setAlignment(ParagraphAlignment.CENTER);
                for (int i = para.getRuns().size() - 1; i >= 0; i--) para.removeRun(i);
                XWPFRun run = para.createRun();
                run.setFontFamily("Times New Roman");
                run.setFontSize(13);
                run.setText(cellValue);
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
