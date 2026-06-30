package com.wmmp.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wmmp.common.exception.BizException;
import com.wmmp.system.dto.UserDTO;
import com.wmmp.system.entity.SysUser;
import com.wmmp.system.mapper.SysRoleMapper;
import com.wmmp.system.mapper.SysUserMapper;
import com.wmmp.system.service.SysUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * SysUserService 单元测试（Mockito）
 * 覆盖：新增用户、删除保护超管、查询
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysUserService 单元测试")
class SysUserServiceTest {
    @Mock private SysUserMapper userMapper;
    @Mock private SysRoleMapper roleMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JdbcTemplate jdbcTemplate;
    @InjectMocks private SysUserService userService;

    private UserDTO buildDTO(String username, String password) {
        UserDTO dto = new UserDTO(); dto.setUsername(username); dto.setPassword(password);
        dto.setRealName("测试用户"); dto.setStatus(1); return dto;
    }

    @Test
    @DisplayName("新增用户：用户名已存在时抛出 BizException")
    void save_duplicateUsername_shouldThrow() {
        given(userMapper.selectCount(ArgumentMatchers.<LambdaQueryWrapper<SysUser>>any())).willReturn(1L);
        assertThatThrownBy(() -> userService.save(buildDTO("admin", "pass")))
                .isInstanceOf(BizException.class).hasMessageContaining("用户名已存在");
    }

    @Test
    @DisplayName("新增用户：密码为空时抛出 BizException")
    void save_emptyPassword_shouldThrow() {
        given(userMapper.selectCount(ArgumentMatchers.<LambdaQueryWrapper<SysUser>>any())).willReturn(0L);
        assertThatThrownBy(() -> userService.save(buildDTO("newuser", "")))
                .isInstanceOf(BizException.class).hasMessageContaining("新增用户密码不能为空");
    }

    @Test
    @DisplayName("新增用户：正常流程调用 insert")
    void save_valid_shouldInsertUser() {
        given(userMapper.selectCount(ArgumentMatchers.<LambdaQueryWrapper<SysUser>>any())).willReturn(0L);
        given(passwordEncoder.encode("Admin@123")).willReturn("$2a$encoded");
        given(userMapper.insert(any(SysUser.class))).willReturn(1);
        userService.save(buildDTO("newuser", "Admin@123"));
        then(userMapper).should().insert(ArgumentMatchers.<SysUser>argThat(u ->
                "newuser".equals(u.getUsername()) && "$2a$encoded".equals(u.getPassword())));
    }

    @Test
    @DisplayName("删除用户：ID=1（超管）时抛出 BizException")
    void delete_superAdmin_shouldThrow() {
        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(BizException.class).hasMessageContaining("不能删除超级管理员");
    }

    @Test
    @DisplayName("删除用户：普通用户正常删除")
    void delete_normalUser_shouldCallDeleteById() {
        given(userMapper.deleteById(2L)).willReturn(1);
        userService.delete(2L); then(userMapper).should().deleteById(2L);
    }

    @Test
    @DisplayName("getById：用户不存在时抛出 BizException")
    void getById_notFound_shouldThrow() {
        given(userMapper.selectById(99L)).willReturn(null);
        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(BizException.class).hasMessageContaining("用户不存在");
    }

    @Test
    @DisplayName("getById：用户存在时正确返回")
    void getById_found_shouldReturnUser() {
        SysUser user = new SysUser(); user.setId(2L); user.setUsername("tom");
        given(userMapper.selectById(2L)).willReturn(user);
        assertThat(userService.getById(2L).getUsername()).isEqualTo("tom");
    }
}
