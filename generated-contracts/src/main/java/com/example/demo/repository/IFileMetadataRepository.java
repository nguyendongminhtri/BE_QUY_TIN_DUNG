package com.example.demo.repository;

import com.example.demo.model.FileMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IFileMetadataRepository extends JpaRepository<FileMetadataEntity, Long> {
    // Xóa theo tên file
    void deleteByFileName(String fileName);
    @Transactional
    void deleteByCreatedAtBefore(LocalDateTime time);
    // Nếu muốn tìm trước rồi xóa
    Optional<FileMetadataEntity> findByFileName(String fileName);
}
