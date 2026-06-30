package com.wmmp.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 文件记录实体 */
@Data
@TableName("sys_file")
public class SysFile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private Long uploadUser;
    private LocalDateTime uploadTime;
    @TableLogic
    private Integer deleted;
}
