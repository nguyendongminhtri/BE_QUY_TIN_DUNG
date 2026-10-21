package com.example.demo.config;

import com.example.demo.repository.IFileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Component
public class FileCleanupScheduler {

    @Value("${contract.files.dir}")
    private String contractFilesDir;
    @Value("${contract.temp.dir}")
    private String contractTempDir;

    @Autowired
    private IFileMetadataRepository fileMetadataRepository;
    @Scheduled(cron = "0 0/30 * * * ?")
    public void cleanUpFiles() {
        deleteOlderThan(contractFilesDir, Duration.ofHours(1));
        deleteOlderThan(contractTempDir, Duration.ofHours(24));

        try {
            fileMetadataRepository.deleteByCreatedAtBefore(LocalDateTime.now().minusHours(24));
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa dữ liệu avatar_temp: " + e.getMessage());
        }
    }
    private void deleteOlderThan(String dirPath, Duration maxAge) {
        Path dir = Paths.get(dirPath);
        if (!Files.isDirectory(dir)) return;

        Instant threshold = Instant.now().minus(maxAge);
        try (Stream<Path> files = Files.list(dir)) {
            files.filter(Files::isRegularFile).forEach(path -> {
                try {
                    if (Files.getLastModifiedTime(path).toInstant().isBefore(threshold)) {
                        Files.deleteIfExists(path);
                    }
                } catch (IOException e) {
                    System.err.println("Không thể xóa file: " + path + " - " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.err.println("Lỗi khi dọn dẹp thư mục " + dirPath + ": " + e.getMessage());
        }
    }

}