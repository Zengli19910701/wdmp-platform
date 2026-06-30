package com.wmmp.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmmp.common.exception.BizException;
import com.wmmp.common.exception.GlobalExceptionHandler;
import com.wmmp.system.controller.UserController;
import com.wmmp.system.dto.UserDTO;
import com.wmmp.system.entity.SysUser;
import com.wmmp.system.service.SysUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 接口测试（MockMvc）
 * 覆盖：分页查询、新增、修改、删除、异常场景
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserController 接口测试")
class UserControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock private SysUserService userService;
    @InjectMocks private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    @DisplayName("GET /api/system/users - 分页查询返回 200")
    void page_shouldReturn200WithPageResult() throws Exception {
        SysUser user = new SysUser(); user.setId(1L); user.setUsername("admin"); user.setStatus(1);
        Page<SysUser> page = new Page<>(1, 10); page.setTotal(1); page.setRecords(List.of(user));
        given(userService.page(anyInt(), anyInt(), anyString())).willReturn(page);
        mockMvc.perform(get("/api/system/users").param("pageNum", "1").param("pageSize", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].username").value("admin"));
    }

    @Test
    @DisplayName("POST /api/system/users - 新增用户成功返回 200")
    void save_shouldReturn200() throws Exception {
        UserDTO dto = new UserDTO(); dto.setUsername("newuser"); dto.setPassword("Pass@123"); dto.setRealName("新用户"); dto.setStatus(1);
        willDoNothing().given(userService).save(any(UserDTO.class));
        mockMvc.perform(post("/api/system/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("POST /api/system/users - 用户名已存在返回业务错误")
    void save_duplicate_shouldReturnBizError() throws Exception {
        UserDTO dto = new UserDTO(); dto.setUsername("admin"); dto.setPassword("Pass@123"); dto.setRealName("重复"); dto.setStatus(1);
        willThrow(new BizException("用户名已存在")).given(userService).save(any(UserDTO.class));
        mockMvc.perform(post("/api/system/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("用户名已存在"));
    }

    @Test
    @DisplayName("DELETE /api/system/users/{id} - 删除超管返回业务错误")
    void delete_superAdmin_shouldReturnBizError() throws Exception {
        willThrow(new BizException("不能删除超级管理员")).given(userService).delete(1L);
        mockMvc.perform(delete("/api/system/users/1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("不能删除超级管理员"));
    }

    @Test
    @DisplayName("DELETE /api/system/users/{id} - 普通用户删除成功返回 200")
    void delete_normalUser_shouldReturn200() throws Exception {
        willDoNothing().given(userService).delete(2L);
        mockMvc.perform(delete("/api/system/users/2"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
}
