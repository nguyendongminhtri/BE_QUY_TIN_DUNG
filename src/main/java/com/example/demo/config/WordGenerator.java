package com.example.demo.config;

import com.example.demo.dto.request.ContractRequest;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class WordGenerator {
    public byte[] generateWord(ContractRequest dto, String fileType) {
        // Tùy theo fileType mà build nội dung khác nhau
        try (XWPFDocument doc = new XWPFDocument()) {
            XWPFParagraph p = doc.createParagraph();
            XWPFRun run = p.createRun();

            if ("file1".equals(fileType)) {
                run.setText("HỢP ĐỒNG TÍN DỤNG");
                run.addBreak();
                run.setText("Khách hàng: " + dto.getTenKhachHang());
            } else if ("file2".equals(fileType)) {
                run.setText("PHỤ LỤC HỢP ĐỒNG");
            } else if ("file3".equals(fileType)) {
                run.setText("BIÊN BẢN ĐỊNH GIÁ");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi sinh file Word", e);
        }
    }
}
