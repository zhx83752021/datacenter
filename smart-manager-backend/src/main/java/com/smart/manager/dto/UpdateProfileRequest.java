package com.smart.manager.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String realName;
    private String mobile;
    private String email;
}
