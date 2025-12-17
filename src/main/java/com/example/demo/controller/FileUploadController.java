    package com.example.demo.controller;

    import com.example.demo.model.FileMetadataEntity;
    import com.example.demo.repository.IFileMetadataRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;
    import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

    import java.io.IOException;
    import java.net.URLDecoder;
    import java.net.URLEncoder;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    @RestController
    @RequestMapping("/files")
    @CrossOrigin(origins = "*")
    public class FileUploadController {

        @Value("${contract.temp.dir}")
        private String tempDir;

        @Autowired
        private IFileMetadataRepository fileMetadataRepository;
        @PostMapping("/upload")
        public ResponseEntity<List<FileMetadataEntity>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
            List<FileMetadataEntity> metadataList = new ArrayList<>();

            for (MultipartFile file : files) {
                try {
                    // Decode tên file gốc để lưu trên ổ đĩa
                    String originalFileName = URLDecoder.decode(file.getOriginalFilename(), StandardCharsets.UTF_8);
                    String fileName = System.currentTimeMillis() + "_" + originalFileName;

                    // Ghi file vào tempDir
                    Path path = Paths.get(tempDir, fileName);
                    Files.createDirectories(path.getParent());
                    Files.write(path, file.getBytes());

                    FileMetadataEntity metadata = new FileMetadataEntity();
                    metadata.setFileName(fileName);
                    metadata.setFilePath(path.toString()); // đường dẫn vật lý
                    metadata.setStatus("TEMP");
                    metadata.setCreatedAt(LocalDateTime.now());

                    // Tạo URL public (KHÔNG encode trước, để UriComponentsBuilder tự encode)
                    String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/temp/")
                            .path(fileName)   // để nguyên tên file
                            .toUriString();
                    metadata.setFilePath(fileUrl); // dùng field riêng cho URL

                    fileMetadataRepository.save(metadata);
                    metadataList.add(metadata);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return ResponseEntity.ok(metadataList);
        }

    }
