package com.wmmp.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 定时任务实体 */
@Data
@TableName("sys_job")
public class SysJob {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String jobName;
    private String jobGroup;
    private String cronExpr;
    private String beanName;
    private String methodName;
    private String params;
    private Integer status;
    private String remark;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
