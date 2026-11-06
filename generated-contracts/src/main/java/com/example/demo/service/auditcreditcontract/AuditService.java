package com.example.demo.service.auditcreditcontract;

import com.example.demo.dto.response.UserStatDto;
import com.example.demo.model.AuditAction;
import com.example.demo.model.CreditContractAuditEntity;
import com.example.demo.model.User;
import com.example.demo.repository.ICreditContractAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuditService {
    @Autowired
    private ICreditContractAuditRepository auditRepo;

    public void log(Long contractId, User user, AuditAction action) {
        CreditContractAuditEntity a = new CreditContractAuditEntity();
        a.setContractId(contractId);
        a.setUser(user);
        a.setAction(action);
        a.setActionAt(LocalDateTime.now());
        auditRepo.save(a);
    }
    public List<UserStatDto> statsByUser(LocalDateTime from, LocalDateTime to) {
        Map<Long, Long> updatedMap = auditRepo.countUpdatedContracts(from, to).stream()
                .collect(Collectors.toMap(
                        ICreditContractAuditRepository.UpdatedContractCount::getUserId,
                        ICreditContractAuditRepository.UpdatedContractCount::getUpdatedContracts));

        return auditRepo.statsByUser(from, to).stream()
                .map(s -> new UserStatDto(
                        s.getUserId(), s.getUsername(), s.getCreated(),
                        updatedMap.getOrDefault(s.getUserId(), 0L),
                        s.getUpdateActions(), s.getDeleted()))
                .toList();
    }
}
