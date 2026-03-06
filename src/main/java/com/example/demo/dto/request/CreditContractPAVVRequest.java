package com.example.demo.dto.request;

import lombok.Data;

@Data
public class CreditContractPAVVRequest {
    private String name;
    private String address;
    private String reason;
    private Boolean checkAddress;
}
