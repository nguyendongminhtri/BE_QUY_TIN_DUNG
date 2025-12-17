package com.example.demo.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class TableRequest {
    private boolean drawTable;
    private List<String> headers;
    private List<List<String>> rows;
}
