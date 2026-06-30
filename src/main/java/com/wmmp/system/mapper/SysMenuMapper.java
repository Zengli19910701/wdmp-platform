package com.wmmp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wmmp.system.entity.SysMenu;
import org.apache.ibatis.annotations.*;
import java.util.List;

/** 菜单 Mapper */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    @Select("SELECT DISTINCT m.* FROM sys_menu m " +
        "JOIN sys_role_menu rm ON m.id = rm.menu_id " +
        "JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
        "WHERE ur.user_id = #{userId} AND m.deleted = 0 AND m.status = 1 AND m.menu_type IN (1,2) " +
        "ORDER BY m.parent_id, m.order_num")
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);

    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
}
