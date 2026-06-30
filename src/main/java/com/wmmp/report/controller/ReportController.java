package com.wmmp.report.controller;

import com.wmmp.common.result.PageResult;
import com.wmmp.common.result.R;
import com.wmmp.report.entity.SysReport;
import com.wmmp.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/** 报表管理接口 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping
    public R<PageResult<SysReport>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String reportName) {
        return R.ok(PageResult.of(reportService.page(pageNum, pageSize, reportName)));
    }

    @GetMapping("/{id}")
    public R<SysReport> getById(@PathVariable Long id) { return R.ok(reportService.getById(id)); }

    @GetMapping("/{id}/data")
    public R<List<Map<String, Object>>> queryData(@PathVariable Long id) { return R.ok(reportService.queryData(id)); }

    @PostMapping
    public R<Void> save(@RequestBody SysReport report) { reportService.save(report); return R.ok(); }

    @PutMapping
    public R<Void> update(@RequestBody SysReport report) { reportService.update(report); return R.ok(); }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) { reportService.delete(id); return R.ok(); }
}
