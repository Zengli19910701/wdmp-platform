package com.wmmp.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmmp.auth.controller.AuthController;
import com.wmmp.auth.dto.LoginRequest;
import com.wmmp.auth.dto.LoginResponse;
import com.wmmp.auth.service.AuthService;
import com.wmmp.common.exception.BizException;
import com.wmmp.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 接口测试（MockMvc）
 * 覆盖：登录成功/失败/参数校验、登出接口
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 接口测试")
class AuthControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock private AuthService authService;
    @InjectMocks private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    @DisplayName("POST /api/auth/login - 正常登录返回 200 和 Token")
    void login_success_should200() throws Exception {
        LoginRequest req = new LoginRequest(); req.setUsername("admin"); req.setPassword("Admin@123");
        LoginResponse resp = LoginResponse.builder().token("mock-token").userId(1L).username("admin")
                .realName("系统管理员").roles(List.of("admin")).perms(List.of("system:user:list")).build();
        given(authService.login(any())).willReturn(resp);
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("mock-token"));
    }

    @Test
    @DisplayName("POST /api/auth/login - 密码错误返回 401")
    void login_wrongPassword_should401() throws Exception {
        LoginRequest req = new LoginRequest(); req.setUsername("admin"); req.setPassword("wrong");
        given(authService.login(any())).willThrow(new BizException(401, "用户名或密码错误"));
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("POST /api/auth/login - 用户名为空返回 400")
    void login_blankUsername_should400() throws Exception {
        LoginRequest req = new LoginRequest(); req.setUsername(""); req.setPassword("Admin@123");
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("POST /api/auth/logout - 登出接口返回 200")
    void logout_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
}
