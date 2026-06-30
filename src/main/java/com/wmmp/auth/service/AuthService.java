package com.wmmp.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wmmp.auth.dto.LoginRequest;
import com.wmmp.auth.dto.LoginResponse;
import com.wmmp.common.exception.BizException;
import com.wmmp.common.utils.JwtUtil;
import com.wmmp.system.entity.SysUser;
import com.wmmp.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

/** 认证服务 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final SysUserMapper sysUserMapper;

    /** 登录：验证账号密码，生成 JWT Token */
    public LoginResponse login(LoginRequest req) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BizException(401, "用户名或密码错误");
        } catch (Exception e) {
            throw new BizException(401, "登录失败: " + e.getMessage());
        }
        SysUser user = findByUsername(req.getUsername());
        return buildResponse(user).token(jwtUtil.generateToken(user.getUsername())).build();
    }

    /** 获取当前登录用户信息 */
    public LoginResponse getUserInfo(String username) {
        return buildResponse(findByUsername(username)).build();
    }

    private SysUser findByUsername(String username) {
        SysUser user = sysUserMapper.selectOne(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) throw new BizException(401, "用户不存在");
        return user;
    }

    private LoginResponse.LoginResponseBuilder buildResponse(SysUser user) {
        return LoginResponse.builder()
            .userId(user.getId()).username(user.getUsername())
            .realName(user.getRealName()).avatar(user.getAvatar())
            .roles(sysUserMapper.selectRoleKeysByUserId(user.getId()))
            .perms(sysUserMapper.selectPermsByUserId(user.getId()));
    }
}
