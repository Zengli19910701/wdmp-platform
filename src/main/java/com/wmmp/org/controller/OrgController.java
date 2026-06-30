package com.wmmp.org.controller;

import com.wmmp.common.result.R;
import com.wmmp.org.entity.SysOrg;
import com.wmmp.org.service.OrgService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** 组织机构接口 */
@RestController
@RequestMapping("/api/orgs")
@RequiredArgsConstructor
public class OrgController {
    private final OrgService orgService;

    @GetMapping
    public R<List<OrgService.OrgTreeVO>> listTree() { return R.ok(orgService.listTree()); }

    @PostMapping
    public R<Void> save(@RequestBody SysOrg org) { orgService.save(org); return R.ok(); }

    @PutMapping
    public R<Void> update(@RequestBody SysOrg org) { orgService.update(org); return R.ok(); }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) { orgService.delete(id); return R.ok(); }
}
