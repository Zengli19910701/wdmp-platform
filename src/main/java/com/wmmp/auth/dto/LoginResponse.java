package com.wmmp.auth.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/** 登录成功响应 DTO */
@Data
@Builder
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String realName;
    private String avatar;
    private List<String> roles;
    private List<String> perms;
}
