package com.example.demo.dto.request;

import lombok.Data;

import java.util.List;
@Data
public class MergeInfoRequest {
    private int rowIndex;          // chỉ số dòng được merge
    private List<String> mergeTargets; // danh sách cột merge
    private String mergedValue;
}
