package com.wmmp.common;

import com.wmmp.common.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtUtil 单元测试")
class JwtUtilTest {
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "test-secret-key-for-unit-test-only-32chars");
        ReflectionTestUtils.setField(jwtUtil, "expire", 3600L);
    }

    @Test
    @DisplayName("生成 Token 应不为空")
    void generateToken_shouldNotBeBlank() {
        String token = jwtUtil.generateToken(1L, "admin");
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("解析 Token 中的用户名")
    void parseUsername_shouldReturnCorrectName() {
        String token = jwtUtil.generateToken(1L, "admin");
        assertThat(jwtUtil.getUsername(token)).isEqualTo("admin");
    }

    @Test
    @DisplayName("有效 Token 校验应通过")
    void validateToken_shouldReturnTrue() {
        String token = jwtUtil.generateToken(1L, "admin");
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("无效 Token 校验应返回 false")
    void validateToken_invalidToken_shouldReturnFalse() {
        assertThat(jwtUtil.validateToken("invalid.token.here")).isFalse();
    }

    @Test
    @DisplayName("解析 Token 中的用户ID")
    void getUserId_shouldReturnCorrectId() {
        String token = jwtUtil.generateToken(42L, "user42");
        assertThat(jwtUtil.getUserId(token)).isEqualTo(42L);
    }
}
