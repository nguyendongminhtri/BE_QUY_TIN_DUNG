package com.example.demo.config;

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
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + contractFilesDir + "/");
        // Cho file upload multiple
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + contractUploadsDir + "/");
        registry.addResourceHandler("/temp/**")
                .addResourceLocations("file:" + tempDir + "/");

    }
}
