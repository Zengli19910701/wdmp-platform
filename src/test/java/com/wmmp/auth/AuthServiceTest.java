package com.wmmp.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wmmp.auth.dto.LoginRequest;
import com.wmmp.auth.dto.LoginResponse;
import com.wmmp.auth.service.AuthService;
import com.wmmp.common.exception.BizException;
import com.wmmp.common.utils.JwtUtil;
import com.wmmp.system.entity.SysUser;
import com.wmmp.system.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 单元测试")
class AuthServiceTest {
    @Mock private SysUserMapper userMapper;
    @Mock private JwtUtil jwtUtil;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks private AuthService authService;

    private SysUser mockUser;
    private LoginRequest loginReq;

    @BeforeEach
    void setUp() {
        mockUser = new SysUser();
        mockUser.setId(1L);
        mockUser.setUsername("admin");
        mockUser.setPassword("encoded-password");
        mockUser.setStatus(1);

        loginReq = new LoginRequest();
        loginReq.setUsername("admin");
        loginReq.setPassword("Admin@123");
    }

    @Test
    @DisplayName("登录成功应返回 Token")
    void login_success_shouldReturnToken() {
        given(userMapper.selectOne(any(LambdaQueryWrapper.class))).willReturn(mockUser);
        given(passwordEncoder.matches("Admin@123", "encoded-password")).willReturn(true);
        given(jwtUtil.generateToken(1L, "admin")).willReturn("mock-token");
        given(userMapper.selectRoleKeys(1L)).willReturn(List.of("admin"));
        given(userMapper.selectPermissions(1L)).willReturn(List.of("system:user:list"));

        LoginResponse resp = authService.login(loginReq);

        assertThat(resp.getToken()).isEqualTo("mock-token");
        assertThat(resp.getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("用户不存在应抛出 BizException")
    void login_userNotFound_shouldThrow() {
        given(userMapper.selectOne(any(LambdaQueryWrapper.class))).willReturn(null);
        assertThatThrownBy(() -> authService.login(loginReq))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("用户名或密码错误");
    }

    @Test
    @DisplayName("密码错误应抛出 BizException")
    void login_wrongPassword_shouldThrow() {
        given(userMapper.selectOne(any(LambdaQueryWrapper.class))).willReturn(mockUser);
        given(passwordEncoder.matches(any(), any())).willReturn(false);
        assertThatThrownBy(() -> authService.login(loginReq))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("用户名或密码错误");
    }

    @Test
    @DisplayName("帐号禁用应抛出 BizException")
    void login_disabledUser_shouldThrow() {
        mockUser.setStatus(0);
        given(userMapper.selectOne(any(LambdaQueryWrapper.class))).willReturn(mockUser);
        given(passwordEncoder.matches(any(), any())).willReturn(true);
        assertThatThrownBy(() -> authService.login(loginReq))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("账号已禁用");
    }
}
