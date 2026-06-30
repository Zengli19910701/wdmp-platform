package com.wmmp.system.controller;

import com.wmmp.common.result.R;
import com.wmmp.system.dto.DeptTreeVO;
import com.wmmp.system.entity.SysDept;
import com.wmmp.system.service.SysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** 部门管理接口 */
@RestController
@RequestMapping("/api/system/depts")
@RequiredArgsConstructor
public class DeptController {
    private final SysDeptService deptService;

    @GetMapping
    public R<List<DeptTreeVO>> listTree() { return R.ok(deptService.listTree()); }

    @PostMapping
    public R<Void> save(@RequestBody SysDept dept) { deptService.save(dept); return R.ok(); }

    @PutMapping
    public R<Void> update(@RequestBody SysDept dept) { deptService.update(dept); return R.ok(); }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) { deptService.delete(id); return R.ok(); }
}
