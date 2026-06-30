package com.wmmp.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wmmp.system.entity.SysUser;
import com.wmmp.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Security 工具类：获取当前登录用户信息 */
@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final SysUserMapper userMapper;

    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    public Long getCurrentUserId() {
        String username = getCurrentUsername();
        if (username == null) return null;
        SysUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username).select(SysUser::getId));
        return user != null ? user.getId() : null;
    }
}
