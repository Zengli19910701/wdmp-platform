package com.wmmp.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/** JWT 工具类：生成、解析、校验 Token */
@Slf4j
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expire}")
    private long expire;

    /** 根据用户名生成 JWT Token */
    public String generateToken(String username) {
        Date now = new Date();
        return Jwts.builder().subject(username).issuedAt(now)
            .expiration(new Date(now.getTime() + expire * 1000L))
            .signWith(getSecretKey()).compact();
    }

    /** 从 Token 中解析用户名 */
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /** 校验 Token 是否有效 */
    public boolean validateToken(String token) {
        try {
            return !getClaims(token).getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT Token 无效: {}", e.getMessage());
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build()
            .parseSignedClaims(token).getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
