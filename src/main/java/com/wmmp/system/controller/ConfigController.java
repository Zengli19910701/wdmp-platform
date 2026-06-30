package com.wmmp.system.controller;

import com.wmmp.common.result.PageResult;
import com.wmmp.common.result.R;
import com.wmmp.system.entity.SysConfig;
import com.wmmp.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 系统配置接口 */
@RestController
@RequestMapping("/api/system/configs")
@RequiredArgsConstructor
public class ConfigController {
    private final SysConfigService configService;

    @GetMapping
    public R<PageResult<SysConfig>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String configName) {
        return R.ok(PageResult.of(configService.page(pageNum, pageSize, configName)));
    }

    @GetMapping("/value")
    public R<String> getValue(@RequestParam String key) { return R.ok(configService.getValueByKey(key)); }

    @PostMapping
    public R<Void> save(@RequestBody SysConfig config) { configService.save(config); return R.ok(); }

    @PutMapping
    public R<Void> update(@RequestBody SysConfig config) { configService.update(config); return R.ok(); }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) { configService.delete(id); return R.ok(); }
}
