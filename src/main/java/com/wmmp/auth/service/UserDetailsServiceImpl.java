package com.wmmp.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wmmp.system.entity.SysUser;
import com.wmmp.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;

/** Spring Security 用户详情服务 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .select(SysUser::getId, SysUser::getUsername, SysUser::getPassword,
                        SysUser::getStatus, SysUser::getDeleted));
        if (user == null) throw new UsernameNotFoundException("用户不存在: " + username);
        List<SimpleGrantedAuthority> authorities = sysUserMapper.selectPermsByUserId(user.getId())
            .stream().filter(StringUtils::hasText).map(SimpleGrantedAuthority::new).toList();
        return User.builder().username(user.getUsername())
            .password(user.getPassword() != null ? user.getPassword() : "")
            .disabled(user.getStatus() != 1)
            .accountExpired(false).credentialsExpired(false).accountLocked(false)
            .authorities(authorities).build();
    }
}
