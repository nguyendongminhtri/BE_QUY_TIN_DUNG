package com.example.demo.controller;

import com.example.demo.dto.request.ContractRequest;
import com.example.demo.service.creditcontract.ICreditContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/credit-contract")
public class CreditContractController {
    @Autowired
    private ICreditContractService creditContractService;
    @PostMapping()
    public ResponseEntity<List<String>> generate(@RequestBody ContractRequest request) throws IOException {
        List<String> filePaths = creditContractService.generateContractFiles(request);
        return ResponseEntity.ok(filePaths);
    }

}
