package com.kheyma.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private Integer age;
    private String phoneNumber;
}

