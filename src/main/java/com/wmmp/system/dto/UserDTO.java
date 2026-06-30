package com.wmmp.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

/** 用户新增/编辑 DTO */
@Data
public class UserDTO {
    private Long id;
    private Long deptId;
    @NotBlank(message = "用户名不能为空")
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Integer status;
    private List<Long> roleIds;
}
