package com.wmmp.system.controller;

import com.wmmp.common.result.PageResult;
import com.wmmp.common.result.R;
import com.wmmp.system.dto.UserDTO;
import com.wmmp.system.entity.SysUser;
import com.wmmp.system.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 用户管理接口 */
@RestController
@RequestMapping("/api/system/users")
@RequiredArgsConstructor
public class UserController {
    private final SysUserService userService;

    @GetMapping
    public R<PageResult<SysUser>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username) {
        return R.ok(PageResult.of(userService.page(pageNum, pageSize, username)));
    }

    @GetMapping("/{id}")
    public R<SysUser> getById(@PathVariable Long id) { return R.ok(userService.getById(id)); }

    @PostMapping
    public R<Void> save(@Valid @RequestBody UserDTO dto) { userService.save(dto); return R.ok(); }

    @PutMapping
    public R<Void> update(@Valid @RequestBody UserDTO dto) { userService.update(dto); return R.ok(); }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) { userService.delete(id); return R.ok(); }
}
