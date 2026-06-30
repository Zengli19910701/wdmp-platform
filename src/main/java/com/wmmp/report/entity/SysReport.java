package com.wmmp.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 报表定义实体 */
@Data
@TableName("sys_report")
public class SysReport {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String reportName;
    private Integer reportType;
    private String dataSql;
    private String configJson;
    private String remark;
    private Long createUser;
    private Integer status;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
