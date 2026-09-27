package com.example.demo.config;

import com.example.demo.repository.IFileMetadataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Component
public class FileCleanupScheduler {
    @Value("${contract.files.dir}")
    private String contractFilesDir;
    @Value("${contract.temp.dir}")
    private String contractTempDir;
    private IFileMetadataRepository fileMetadataRepository;

    // Cron: giây phút giờ ngày-tháng tháng ngày-trong-tuần
//        @Scheduled(fixedRate = 3000000)
    @Scheduled(cron = "0 0 5 * * ?") // chạy mỗi ngày lúc 5h sáng
    public void cleanUpFiles() {
        System.err.println("=================DỌN DẸP FILE===================");

        // Gom các thư mục cần dọn
        List<String> dirs = List.of(contractFilesDir, contractTempDir);

        for (String dirPath : dirs) {
            Path dir = Paths.get(dirPath);
            try (Stream<Path> files = Files.list(dir)) {
                files.forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                        System.out.println("Đã xóa file: " + path);
                    } catch (IOException e) {
                        System.err.println("Không thể xóa file: " + path + " - " + e.getMessage());
                    }
                });
            } catch (IOException e) {
                System.err.println("Lỗi khi dọn dẹp thư mục " + dirPath + ": " + e.getMessage());
            }
        }
        // Xóa toàn bộ dữ liệu trong bảng avatar_temp
        try {
            fileMetadataRepository.deleteAll();
            System.out.println("Đã xóa toàn bộ dữ liệu trong bảng avatar_temp");
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa dữ liệu avatar_temp: " + e.getMessage());
        }
    }
}
