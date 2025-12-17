package com.example.demo.controller;

import com.example.demo.dto.request.ContractRequest;
import com.example.demo.model.AvatarEntity;
import com.example.demo.model.CategoryEntity;
import com.example.demo.model.CreditContractEntity;
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
import java.util.List;
import java.util.Optional;
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

    /**
     * 👉 Export hợp đồng mới (create + lưu DB)
     */
    @PostMapping("/export")
    public ResponseEntity<Resource> export(@RequestBody ContractRequest request) throws IOException {
        List<String> filePaths = creditContractService.generateContractFilesExport(request);
        return buildZipResponse(filePaths);
    }

    /**
     * 👉 Export hợp đồng đã có (update + lưu DB)
     */
    @PostMapping("/export/{id}")
    public ResponseEntity<Resource> exportUpdate(@PathVariable Long id,
                                                 @RequestBody ContractRequest request) throws IOException {
        List<String> filePaths = creditContractService.updateContractFilesExport(id, request);
        return buildZipResponse(filePaths);
    }

    @GetMapping
    public ResponseEntity<?> getListCreditContracts() {
        return new ResponseEntity<>(creditContractService.findAll(), HttpStatus.OK);
    }
    private ResponseEntity<Resource> buildZipResponse(List<String> filePaths) throws IOException {
        String zipFileName = "contracts_" + System.currentTimeMillis() + ".zip";
        Path zipPath = Paths.get(contractFilesDir, zipFileName);
        Files.createDirectories(zipPath.getParent());

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            for (String filePath : filePaths) {
                Path path = Paths.get(filePath);
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
    @GetMapping("/{id}")
    public ResponseEntity<?> detailCategory(@PathVariable Long id){
        Optional<CreditContractEntity> creditContractEntity = creditContractService.findById(id);
        if(!creditContractEntity.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(creditContractEntity, HttpStatus.OK);
    }

}
