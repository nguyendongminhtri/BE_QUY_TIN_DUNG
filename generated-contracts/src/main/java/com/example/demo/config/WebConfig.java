package com.example.demo.config;

import com.example.demo.service.user.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${contract.files.dir}")
    private String contractFilesDir;

    @Value("${contract.uploads.dir}")
    private String contractUploadsDir;

    @Value("${contract.temp.dir}")
    private String tempDir;

    private final FileStorageService fileStorageService;

    public WebConfig(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + contractFilesDir + "/");

        // Cho file upload multiple (giữ nguyên)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + contractUploadsDir + "/");

        registry.addResourceHandler("/temp/**")
                .addResourceLocations("file:" + tempDir + "/");

        // MỚI: ảnh đại diện, lấy từ app.upload-dir/avatars/
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations(fileStorageService.getAvatarDir().toUri().toString());
    }
}