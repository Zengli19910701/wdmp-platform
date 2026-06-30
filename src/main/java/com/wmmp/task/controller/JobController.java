package com.wmmp.task.controller;

import com.wmmp.common.result.PageResult;
import com.wmmp.common.result.R;
import com.wmmp.task.entity.SysJob;
import com.wmmp.task.entity.SysJobLog;
import com.wmmp.task.service.JobService;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

/** 定时任务管理接口 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @GetMapping
    public R<PageResult<SysJob>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String jobName) {
        return R.ok(PageResult.of(jobService.page(pageNum, pageSize, jobName)));
    }

    @PostMapping
    public R<Void> save(@RequestBody SysJob job) throws SchedulerException { jobService.save(job); return R.ok(); }

    @PutMapping
    public R<Void> update(@RequestBody SysJob job) throws SchedulerException { jobService.update(job); return R.ok(); }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) throws SchedulerException { jobService.delete(id); return R.ok(); }

    @PutMapping("/{id}/start")
    public R<Void> start(@PathVariable Long id) throws SchedulerException { jobService.start(id); return R.ok(); }

    @PutMapping("/{id}/stop")
    public R<Void> stop(@PathVariable Long id) throws SchedulerException { jobService.stop(id); return R.ok(); }

    @PostMapping("/{id}/trigger")
    public R<Void> trigger(@PathVariable Long id) throws SchedulerException { jobService.trigger(id); return R.ok(); }

    @GetMapping("/logs")
    public R<PageResult<SysJobLog>> logs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long jobId) {
        return R.ok(PageResult.of(jobService.logPage(pageNum, pageSize, jobId)));
    }
}
