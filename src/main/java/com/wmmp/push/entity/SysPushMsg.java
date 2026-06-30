package com.wmmp.push.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 消息推送实体 */
@Data
@TableName("sys_push_msg")
public class SysPushMsg {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private Integer msgType;
    private Integer targetType;
    private String targetIds;
    private Long sendUser;
    private LocalDateTime sendTime;
    private Integer status;
    @TableLogic
    private Integer deleted;
}
