package com.wmmp.auth.controller;

import com.wmmp.auth.dto.LoginRequest;
import com.wmmp.auth.dto.LoginResponse;
import com.wmmp.auth.service.AuthService;
import com.wmmp.common.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** 认证接口：登录/登出/获取用户信息 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /** 用户登录 */
    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return R.ok(authService.login(req));
    }

    /** 获取当前用户信息 */
    @GetMapping("/info")
    public R<LoginResponse> getUserInfo(Authentication authentication) {
        return R.ok(authService.getUserInfo(authentication.getName()));
    }

    /** 登出 */
    @PostMapping("/logout")
    public R<Void> logout() {
        return R.ok("退出登录成功", null);
    }
}
