package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatDto {
    private Long userId;
    private String username;
    private long created;
    private long updatedContracts;
    private long updateActions;
    private long deleted;
}