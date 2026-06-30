package com.wmmp.system.dto;

import com.wmmp.system.entity.SysMenu;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/** 菜单树形 VO */
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuTreeVO extends SysMenu {
    private List<MenuTreeVO> children;
}
