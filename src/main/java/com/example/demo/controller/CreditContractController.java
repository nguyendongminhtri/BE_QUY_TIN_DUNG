package com.example.demo.controller;

import com.example.demo.dto.request.ContractRequest;
import com.example.demo.model.AvatarEntity;
import com.example.demo.service.creditcontract.ICreditContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/credit-contract")
public class CreditContractController {
    @Autowired
    private ICreditContractService creditContractService;
    @PostMapping()
    public ResponseEntity<List<String>> generate(@RequestBody ContractRequest request) throws IOException {
        List<String> fileUrls = creditContractService.generateContractFilesPreview(request);
        return ResponseEntity.ok(fileUrls);
    }
    @Value("${contract.files.dir}")
    private String contractFilesDir;
    @PostMapping("/export")
    public ResponseEntity<Resource> export(@RequestBody ContractRequest request) throws IOException {
        List<String> filePaths = creditContractService.generateContractFilesExport(request);

        // Tạo file ZIP tạm
        String zipFileName = "contracts_" + System.currentTimeMillis() + ".zip";
        Path zipPath = Paths.get(contractFilesDir, zipFileName);
        Files.createDirectories(zipPath.getParent());

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            for (String filePath : filePaths) {
                Path path = Paths.get(filePath); // ✅ đường dẫn vật lý
                zos.putNextEntry(new ZipEntry(path.getFileName().toString()));
                Files.copy(path, zos);
                zos.closeEntry();
            }
        }

        Resource resource = new UrlResource(zipPath.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


}
