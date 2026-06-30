package com.wmmp.system.controller;

import com.wmmp.common.result.PageResult;
import com.wmmp.common.result.R;
import com.wmmp.system.entity.SysRole;
import com.wmmp.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** 角色管理接口 */
@RestController
@RequestMapping("/api/system/roles")
@RequiredArgsConstructor
public class RoleController {
    private final SysRoleService roleService;

    @GetMapping
    public R<PageResult<SysRole>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String roleName) {
        return R.ok(PageResult.of(roleService.page(pageNum, pageSize, roleName)));
    }

    @GetMapping("/all")
    public R<List<SysRole>> listAll() { return R.ok(roleService.listAll()); }

    @GetMapping("/{id}/menus")
    public R<List<Long>> getMenuIds(@PathVariable Long id) { return R.ok(roleService.getMenuIds(id)); }

    @PostMapping
    public R<Void> save(@RequestBody SysRole role) { roleService.save(role); return R.ok(); }

    @PutMapping
    public R<Void> update(@RequestBody SysRole role) { roleService.update(role); return R.ok(); }

    @PutMapping("/{id}/menus")
    public R<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(id, menuIds); return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) { roleService.delete(id); return R.ok(); }
}
