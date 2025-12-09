package com.example.demo.service.creditcontract;

import com.example.demo.dto.request.ContractRequest;
import com.example.demo.model.AvatarEntity;
import com.example.demo.model.CreditContractEntity;
import com.example.demo.service.IGenericService;

import java.io.IOException;
import java.util.List;

public interface ICreditContractService extends IGenericService<CreditContractEntity> {
     List<String> generateContractFilesPreview(ContractRequest request) throws IOException;
     List<String> generateContractFilesExport(ContractRequest request) throws IOException;
}
