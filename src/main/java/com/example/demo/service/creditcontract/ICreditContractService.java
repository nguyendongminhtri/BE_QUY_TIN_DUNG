package com.example.demo.service.creditcontract;

import com.example.demo.dto.request.ContractRequest;
import com.example.demo.model.CreditContractEntity;
import com.example.demo.service.IGenericService;

import java.io.IOException;
import java.util.List;

public interface ICreditContractService extends IGenericService<CreditContractEntity> {
     List<String> generateContractFiles(ContractRequest request) throws IOException;
}
