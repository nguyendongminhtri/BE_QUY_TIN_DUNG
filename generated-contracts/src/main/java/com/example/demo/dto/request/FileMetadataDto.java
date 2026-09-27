package com.example.demo.dto.request;

import lombok.Data;

@Data
public class FileMetadataDto {
    private String fileName;
    private String fileUrl;
    private String contentType;
}