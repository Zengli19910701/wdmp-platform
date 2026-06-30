package com.wmmp.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wmmp.common.exception.BizException;
import com.wmmp.system.dto.UserDTO;
import com.wmmp.system.entity.SysUser;
import com.wmmp.system.mapper.SysRoleMapper;
import com.wmmp.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 用户服务 */
@Service
@RequiredArgsConstructor
public class SysUserService {
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    /** 分页查询用户列表 */
    public IPage<SysUser> page(int pageNum, int pageSize, String username) {
        return userMapper.selectPage(new Page<>(pageNum, pageSize),
            new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(username), SysUser::getUsername, username)
                .orderByAsc(SysUser::getId));
    }

    /** 根据ID查询用户 */
    public SysUser getById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BizException("用户不存在");
        return user;
    }

    /** 新增用户 */
    @Transactional
    public void save(UserDTO dto) {
        long count = userMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) throw new BizException("用户名已存在");
        if (!StringUtils.hasText(dto.getPassword())) throw new BizException("新增用户密码不能为空");
        SysUser user = new SysUser();
        copyDtoToEntity(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userMapper.insert(user);
        bindRoles(user.getId(), dto);
    }

    /** 编辑用户 */
    @Transactional
    public void update(UserDTO dto) {
        SysUser user = userMapper.selectById(dto.getId());
        if (user == null) throw new BizException("用户不存在");
        copyDtoToEntity(dto, user);
        if (StringUtils.hasText(dto.getPassword())) user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userMapper.updateById(user);
        jdbcTemplate.update("DELETE FROM sys_user_role WHERE user_id = ?", dto.getId());
        bindRoles(dto.getId(), dto);
    }

    /** 删除用户 */
    public void delete(Long id) {
        if (id == 1) throw new BizException("不能删除超级管理员");
        userMapper.deleteById(id);
    }

    private void copyDtoToEntity(UserDTO dto, SysUser user) {
        user.setDeptId(dto.getDeptId()); user.setUsername(dto.getUsername());
        user.setRealName(dto.getRealName()); user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
    }

    private void bindRoles(Long userId, UserDTO dto) {
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            jdbcTemplate.batchUpdate("INSERT INTO sys_user_role(user_id, role_id) VALUES (?, ?)",
                dto.getRoleIds().stream().map(roleId -> new Object[]{userId, roleId}).toList());
        }
    }
}
