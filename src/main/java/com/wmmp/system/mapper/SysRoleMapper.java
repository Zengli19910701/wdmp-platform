package com.wmmp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wmmp.system.entity.SysRole;
import org.apache.ibatis.annotations.*;
import java.util.List;

/** 角色 Mapper */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    @Select("SELECT r.* FROM sys_role r JOIN sys_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);
}
