package com.example.demo.controller;

import com.example.demo.dto.request.ContractRequest;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.mapper.ContractMapper;
import com.example.demo.model.AvatarEntity;
import com.example.demo.model.CarouselEntity;
import com.example.demo.model.CategoryEntity;
import com.example.demo.model.CreditContractEntity;
import com.example.demo.service.creditcontract.ICreditContractService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    @Autowired
    private ContractMapper contractMapper;

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
    public ResponseEntity<List<ContractRequest>> getListCreditContracts() {
        List<CreditContractEntity> entities = creditContractService.findAll();
        List<ContractRequest> dtos = entities.stream()
                .map(entity -> {
                    try {
                        return contractMapper.mapEntityToRequest(entity);
                    } catch (Exception e) {
                        throw new RuntimeException("Không thể parse dữ liệu bảng", e);
                    }
                })
                .toList();
        return ResponseEntity.ok(dtos);
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
    public ResponseEntity<ContractRequest> detailContract(@PathVariable Long id) throws JsonProcessingException {
        CreditContractEntity entity = creditContractService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));
        ContractRequest dto = contractMapper.mapEntityToRequest(entity);
        System.err.println("dto =======> "+dto);
        return ResponseEntity.ok(dto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCarousel(@PathVariable Long id) {
        System.err.println("------------xóa id ---- "+id);
        Optional<CreditContractEntity> carouselEntity = creditContractService.findById(id);
        if(!carouselEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        creditContractService.deleteById(id);
        return new ResponseEntity<>(new ResponMessage("delete_success"), HttpStatus.OK);
    }

}
