package com.example.demo.controller;

import com.example.demo.dto.request.SyncHuyDongRequest;
import com.example.demo.model.KpiKyEntity;
import com.example.demo.repository.IKpiKyRepository;
import com.example.demo.service.huydongKPI.SyncHuyDongService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class KpiSyncController {
    private final IKpiKyRepository kpiRepo;
    private final String token;
    private final SyncHuyDongService service;
    public KpiSyncController(IKpiKyRepository kpiRepo, @Value("${sync.token}") String token,SyncHuyDongService service) {
        this.kpiRepo = kpiRepo;
        this.token = token;
        this.service = service;
    }

    @PostMapping("/kpi-ky")
    @Transactional
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> kpiKy(@RequestHeader(value = "X-Sync-Token", required = false) String key,
                                   @RequestBody Map<String, Object> body) {
        if (token.isBlank() || key == null
                || !MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8),
                key.getBytes(StandardCharsets.UTF_8))) {
            return ResponseEntity.status(401).body(Map.of("error", "sync token sai hoac chua cau hinh"));
        }

        String ky = (String) body.get("ky");
        LocalDate tu = LocalDate.parse((String) body.get("tu"));
        LocalDate den = LocalDate.parse((String) body.get("den"));
        List<Map<String, Object>> rows = (List<Map<String, Object>>) body.get("rows");

        kpiRepo.xoaTheoKy(ky);   // thay thế toàn bộ dòng của kỳ

        LocalDateTime now = LocalDateTime.now();
        List<KpiKyEntity> ds = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            KpiKyEntity e = new KpiKyEntity();
            String nguoiTao = (String) r.get("nguoiTao");
            e.setId(ky + "|" + nguoiTao);
            e.setKy(ky);
            e.setTu(tu);
            e.setDen(den);
            e.setNguoiTao(nguoiTao);
            e.setSoLuot(((Number) r.get("soLuot")).intValue());
            e.setSoKhach(((Number) r.get("soKhach")).intValue());
            e.setSyncedAt(now);
            ds.add(e);
        }
        kpiRepo.saveAll(ds);
        return ResponseEntity.ok(Map.of("received", ds.size()));
    }

    // Xem nhanh KPI một kỳ (sau này nên giới hạn quyền ADMIN, hiện đang chỉ dùng thử)
    @GetMapping("/kpi-ky")
    public List<KpiKyEntity> xem(@RequestParam String ky) {
        return kpiRepo.findByKyOrderBySoLuotDesc(ky);
    }
    @PostMapping("/huy-dong-ky")
    public ResponseEntity<?> huyDongKy(
            @RequestHeader(value = "X-Sync-Token", required = false) String token,
            @RequestBody SyncHuyDongRequest req) {

        if (token == null || !MessageDigest.isEqual(
                token.getBytes(StandardCharsets.UTF_8),
                token.getBytes(StandardCharsets.UTF_8))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Sai token"));
        }
        try {
            int n = service.replaceKy(req);
            return ResponseEntity.ok(Map.of("ky", req.ky(), "saved", n));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
