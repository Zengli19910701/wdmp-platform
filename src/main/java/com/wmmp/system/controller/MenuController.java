package com.wmmp.system.controller;

import com.wmmp.common.result.R;
import com.wmmp.common.utils.SecurityUtil;
import com.wmmp.system.dto.MenuTreeVO;
import com.wmmp.system.entity.SysMenu;
import com.wmmp.system.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** 菜单管理接口 */
@RestController
@RequestMapping("/api/system/menus")
@RequiredArgsConstructor
public class MenuController {
    private final SysMenuService menuService;
    private final SecurityUtil securityUtil;

    @GetMapping
    public R<List<MenuTreeVO>> listTree() { return R.ok(menuService.listTree()); }

    @GetMapping("/nav")
    public R<List<MenuTreeVO>> getUserMenus() { return R.ok(menuService.getUserMenuTree(securityUtil.getCurrentUserId())); }

    @PostMapping
    public R<Void> save(@RequestBody SysMenu menu) { menuService.save(menu); return R.ok(); }

    @PutMapping
    public R<Void> update(@RequestBody SysMenu menu) { menuService.update(menu); return R.ok(); }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) { menuService.delete(id); return R.ok(); }
}
