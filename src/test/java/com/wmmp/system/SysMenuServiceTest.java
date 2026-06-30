package com.wmmp.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wmmp.common.exception.BizException;
import com.wmmp.system.dto.MenuTreeVO;
import com.wmmp.system.entity.SysMenu;
import com.wmmp.system.mapper.SysMenuMapper;
import com.wmmp.system.service.SysMenuService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * SysMenuService 单元测试
 * 覆盖：菜单树构建、删除保护（有子菜单时禁止删除）
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysMenuService 单元测试")
class SysMenuServiceTest {
    @Mock private SysMenuMapper menuMapper;
    @InjectMocks private SysMenuService menuService;

    private List<SysMenu> buildMockMenus() {
        SysMenu root = new SysMenu(); root.setId(1L); root.setParentId(0L); root.setMenuName("系统管理"); root.setOrderNum(1);
        SysMenu c1 = new SysMenu(); c1.setId(2L); c1.setParentId(1L); c1.setMenuName("用户管理"); c1.setOrderNum(1);
        SysMenu c2 = new SysMenu(); c2.setId(3L); c2.setParentId(1L); c2.setMenuName("角色管理"); c2.setOrderNum(2);
        return List.of(root, c1, c2);
    }

    @Test
    @DisplayName("listTree：正确构建两级树形结构")
    void listTree_shouldBuildTwoLevelTree() {
        given(menuMapper.selectList(ArgumentMatchers.any())).willReturn(buildMockMenus());
        List<MenuTreeVO> tree = menuService.listTree();
        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).getMenuName()).isEqualTo("系统管理");
        assertThat(tree.get(0).getChildren()).hasSize(2);
    }

    @Test
    @DisplayName("delete：有子菜单时禁止删除")
    void delete_hasChildren_shouldThrow() {
        given(menuMapper.selectCount(ArgumentMatchers.<LambdaQueryWrapper<SysMenu>>any())).willReturn(2L);
        assertThatThrownBy(() -> menuService.delete(1L))
                .isInstanceOf(BizException.class).hasMessageContaining("存在子菜单");
    }

    @Test
    @DisplayName("delete：无子菜单时正常删除")
    void delete_noChildren_shouldDelete() {
        given(menuMapper.selectCount(ArgumentMatchers.<LambdaQueryWrapper<SysMenu>>any())).willReturn(0L);
        given(menuMapper.deleteById(5L)).willReturn(1);
        menuService.delete(5L);
        then(menuMapper).should().deleteById(5L);
    }
}
