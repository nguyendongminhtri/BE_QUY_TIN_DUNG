package com.example.demo.controller;

import com.example.demo.model.GiaoDichEntity;
import com.example.demo.repository.IGiaoDichRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class SyncController {
    private final IGiaoDichRepository repo;
    private final String token;

    public SyncController(IGiaoDichRepository repo, @Value("${sync.token}") String token) {
        this.repo = repo;
        this.token = token;
    }

    @PostMapping("/giao-dich")
    @Transactional
    public ResponseEntity<?> sync(@RequestHeader(value = "X-Sync-Token", required = false) String key,
                                  @RequestBody List<GiaoDichEntity> rows) {
        System.out.println("[sync] header=" + (key == null ? "null" : key.length())
                + ", token cấu hình=" + token.length() + ", số dòng=" + rows.size());

        if (token.isBlank() || key == null
                || !MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8),
                key.getBytes(StandardCharsets.UTF_8))) {
            return ResponseEntity.status(401).body(Map.of("error", "sync token sai hoac chua cau hinh"));
        }
        LocalDateTime now = LocalDateTime.now();
        rows.forEach(r -> r.setSyncedAt(now));
        repo.saveAll(rows);
        return ResponseEntity.ok(Map.of("received", rows.size()));
    }
}
