package com.wmmp.push.controller;

import com.wmmp.common.result.PageResult;
import com.wmmp.common.result.R;
import com.wmmp.common.utils.SecurityUtil;
import com.wmmp.push.entity.SysPushMsg;
import com.wmmp.push.service.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 消息推送接口 */
@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class PushController {
    private final PushService pushService;
    private final SecurityUtil securityUtil;

    @PostMapping("/send")
    public R<Void> send(@RequestBody SysPushMsg msg) {
        pushService.send(msg, securityUtil.getCurrentUserId()); return R.ok("消息发送成功", null);
    }

    @PostMapping("/draft")
    public R<Void> draft(@RequestBody SysPushMsg msg) {
        pushService.saveDraft(msg, securityUtil.getCurrentUserId()); return R.ok("草稿保存成功", null);
    }

    @GetMapping
    public R<PageResult<SysPushMsg>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(PageResult.of(pushService.page(pageNum, pageSize)));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) { pushService.delete(id); return R.ok(); }
}
