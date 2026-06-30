package com.wmmp.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 任务执行日志实体 */
@Data
@TableName("sys_job_log")
public class SysJobLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long jobId;
    private String jobName;
    private Integer status;
    private String message;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long costMs;
}
