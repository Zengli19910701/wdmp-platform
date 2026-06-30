package com.wmmp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wmmp.system.entity.SysUser;
import org.apache.ibatis.annotations.*;
import java.util.List;

/** 用户 Mapper */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /** 查询用户的角色标识列表 */
    @Select("SELECT r.role_key FROM sys_role r " +
        "JOIN sys_user_role ur ON r.id = ur.role_id " +
        "WHERE ur.user_id = #{userId} AND r.deleted = 0 AND r.status = 1")
    List<String> selectRoleKeysByUserId(@Param("userId") Long userId);

    /** 查询用户的权限标识列表 */
    @Select("SELECT DISTINCT m.perms FROM sys_menu m " +
        "JOIN sys_role_menu rm ON m.id = rm.menu_id " +
        "JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
        "WHERE ur.user_id = #{userId} AND m.deleted = 0 AND m.perms IS NOT NULL AND m.perms != ''")
    List<String> selectPermsByUserId(@Param("userId") Long userId);
}
