package com.wmmp.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wmmp.common.exception.BizException;
import com.wmmp.system.dto.MenuTreeVO;
import com.wmmp.system.entity.SysMenu;
import com.wmmp.system.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;

/** 菜单服务 */
@Service
@RequiredArgsConstructor
public class SysMenuService {
    private final SysMenuMapper menuMapper;

    public List<MenuTreeVO> listTree() {
        List<SysMenu> menus = menuMapper.selectList(
            new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum));
        return buildTree(menus, 0L);
    }

    public List<MenuTreeVO> getUserMenuTree(Long userId) {
        return buildTree(menuMapper.selectMenusByUserId(userId), 0L);
    }

    public void save(SysMenu menu) { menuMapper.insert(menu); }

    public void update(SysMenu menu) {
        if (menuMapper.selectById(menu.getId()) == null) throw new BizException("菜单不存在");
        menuMapper.updateById(menu);
    }

    public void delete(Long id) {
        long childCount = menuMapper.selectCount(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
        if (childCount > 0) throw new BizException("存在子菜单，不能删除");
        menuMapper.deleteById(id);
    }

    private List<MenuTreeVO> buildTree(List<SysMenu> list, Long parentId) {
        return list.stream().filter(m -> parentId.equals(m.getParentId())).map(m -> {
            MenuTreeVO vo = new MenuTreeVO();
            BeanUtils.copyProperties(m, vo);
            List<MenuTreeVO> children = buildTree(list, m.getId());
            if (!children.isEmpty()) vo.setChildren(children);
            return vo;
        }).toList();
    }
}
