package com.wmmp.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wmmp.common.exception.BizException;
import com.wmmp.system.entity.SysRole;
import com.wmmp.system.mapper.SysMenuMapper;
import com.wmmp.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;

/** 角色服务 */
@Service
@RequiredArgsConstructor
public class SysRoleService {
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final JdbcTemplate jdbcTemplate;

    public IPage<SysRole> page(int pageNum, int pageSize, String roleName) {
        return roleMapper.selectPage(new Page<>(pageNum, pageSize),
            new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
                .orderByAsc(SysRole::getOrderNum));
    }

    public List<SysRole> listAll() {
        return roleMapper.selectList(new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, 1).orderByAsc(SysRole::getOrderNum));
    }

    public List<Long> getMenuIds(Long roleId) { return menuMapper.selectMenuIdsByRoleId(roleId); }

    public void save(SysRole role) {
        long count = roleMapper.selectCount(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleKey, role.getRoleKey()));
        if (count > 0) throw new BizException("角色标识已存在");
        roleMapper.insert(role);
    }

    public void update(SysRole role) {
        if (roleMapper.selectById(role.getId()) == null) throw new BizException("角色不存在");
        roleMapper.updateById(role);
    }

    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        jdbcTemplate.update("DELETE FROM sys_role_menu WHERE role_id = ?", roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            jdbcTemplate.batchUpdate("INSERT INTO sys_role_menu(role_id, menu_id) VALUES (?, ?)",
                menuIds.stream().map(id -> new Object[]{roleId, id}).toList());
        }
    }

    public void delete(Long id) {
        long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_user_role WHERE role_id = ?", Long.class, id);
        if (count > 0) throw new BizException("该角色已分配给用户，不能删除");
        roleMapper.deleteById(id);
    }
}
