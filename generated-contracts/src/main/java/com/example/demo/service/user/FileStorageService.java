package com.example.demo.service.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final long MAX_AVATAR_SIZE = 2 * 1024 * 1024; // 2MB
    private static final String AVATAR_URL_PREFIX = "/avatars/";

    private static final Map<String, String> ALLOWED_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "image/gif", ".gif"
    );

    private final Path root;
    private final Path avatarDir;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.avatarDir = root.resolve("avatars").normalize();
        Files.createDirectories(avatarDir);
    }

    public String saveAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new IllegalArgumentException("Ảnh tối đa 2MB");
        }
        String ext = ALLOWED_TYPES.get(file.getContentType());
        if (ext == null) {
            throw new IllegalArgumentException("Chỉ chấp nhận ảnh JPG, PNG, WEBP, GIF");
        }

        // Tên file do server tự sinh, không dùng tên gốc -> tránh path traversal
        String fileName = UUID.randomUUID() + ext;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, avatarDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Không lưu được file", e);
        }
        return AVATAR_URL_PREFIX + fileName;
    }

    public void deleteAvatar(String avatarUrl) {
        if (avatarUrl == null || !avatarUrl.startsWith(AVATAR_URL_PREFIX)) {
            return; // null hoặc link ngoài -> bỏ qua
        }
        Path target = avatarDir.resolve(avatarUrl.substring(AVATAR_URL_PREFIX.length())).normalize();
        if (!target.startsWith(avatarDir)) {
            return; // chặn đường dẫn thoát khỏi thư mục avatars
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
        }
    }

    public Path getRoot() {
        return root;
    }

    public Path getAvatarDir() {
        return avatarDir;
    }
}