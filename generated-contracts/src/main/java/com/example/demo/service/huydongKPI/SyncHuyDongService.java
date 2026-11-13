package com.example.demo.service.huydongKPI;

import com.example.demo.dto.request.SyncHuyDongRequest;
import com.example.demo.model.HuyDongKyEntity;
import com.example.demo.repository.IHuyDongKyRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class SyncHuyDongService {
    private final IHuyDongKyRepository repo;

    public SyncHuyDongService(IHuyDongKyRepository repo) {
        this.repo = repo;
    }

    /**
     * Ghi đè số liệu huy động của một kỳ. Với mỗi chi nhánh có trong payload:
     * xóa dòng cũ của (kỳ, chi nhánh) rồi ghi lại. Chạy lại nhiều lần cho cùng kỳ thì kết quả không đổi.
     */
    @Transactional
    public int replaceKy(SyncHuyDongRequest req) {
        if (req.ky() == null || !req.ky().matches("\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("Kỳ không hợp lệ: " + req.ky());
        }
        if (req.rows() == null || req.rows().isEmpty()) {
            return 0;   // không có dữ liệu thì giữ nguyên số liệu cũ
        }

        Set<String> chiNhanhs = new LinkedHashSet<>();
        for (SyncHuyDongRequest.Row r : req.rows()) {
            if (r.maChiNhanh() == null || r.maTk() == null) {
                throw new IllegalArgumentException("Thiếu maChiNhanh hoặc maTk");
            }
            chiNhanhs.add(r.maChiNhanh().trim());
        }
        for (String cn : chiNhanhs) {
            repo.deleteByKyAndChiNhanh(req.ky(), cn);
        }
        repo.flush();   // đảm bảo xóa xong trước khi insert, tránh vướng unique key

        LocalDateTime now = LocalDateTime.now();
        List<HuyDongKyEntity> list = new ArrayList<>();
        for (SyncHuyDongRequest.Row r : req.rows()) {
            HuyDongKyEntity h = new HuyDongKyEntity();
            h.setKy(req.ky());
            h.setMaChiNhanh(r.maChiNhanh().trim());
            h.setMaTk(r.maTk().trim());
            h.setTenTk(r.tenTk());
            h.setTuNgay(req.tu());
            h.setDenNgay(req.den());
            h.setDuDauKy(r.duDauKy());
            h.setPsNo(r.psNo());
            h.setPsCo(r.psCo());
            h.setDuCuoiKy(r.duCuoiKy());
            h.setTangRong(r.tangRong());
            h.setUpdatedAt(now);
            list.add(h);
        }
        repo.saveAll(list);
        return list.size();
    }
}
