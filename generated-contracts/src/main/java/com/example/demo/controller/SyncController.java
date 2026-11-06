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

    public SyncController(IGiaoDichRepository repo, @Value("${sync.token:QTDTH_Sync_SecretKey_Secure_998877}") String token) {
        this.repo = repo;
        this.token = token;
    }

    @PostMapping("/giao-dich")
    @Transactional
    public ResponseEntity<?> sync(@RequestHeader(value = "Authorization", required = false) String auth,
                                  @RequestBody List<GiaoDichEntity> rows) {
        byte[] expected = ("Bearer " + token).getBytes(StandardCharsets.UTF_8);
        byte[] actual = (auth == null ? "" : auth).getBytes(StandardCharsets.UTF_8);
        if (token.isBlank() || !MessageDigest.isEqual(expected, actual)) {
            return ResponseEntity.status(401).build();
        }
        LocalDateTime now = LocalDateTime.now();
        rows.forEach(r -> r.setSyncedAt(now));
        repo.saveAll(rows);   // soCt đã có thì cập nhật, chưa có thì thêm
        return ResponseEntity.ok(Map.of("received", rows.size()));
    }
}
