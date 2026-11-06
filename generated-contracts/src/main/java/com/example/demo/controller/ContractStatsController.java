package com.example.demo.controller;

import com.example.demo.dto.response.UserStatDto;
import com.example.demo.service.auditcreditcontract.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/contract-stats")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAuthority('ADMIN')")
public class ContractStatsController {

    @Autowired
    private AuditService auditService;

    @GetMapping
    public List<UserStatDto> stats(@RequestParam int year,
                                   @RequestParam(required = false) Integer month) {
        if (month != null && (month < 1 || month > 12)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "month phải từ 1 đến 12");
        }
        if (year < 2000 || year > 2100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "year không hợp lệ");
        }

        LocalDateTime from = (month != null)
                ? LocalDate.of(year, month, 1).atStartOfDay()
                : LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime to = (month != null) ? from.plusMonths(1) : from.plusYears(1);

        return auditService.statsByUser(from, to);
    }
}