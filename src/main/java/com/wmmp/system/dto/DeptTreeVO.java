package com.wmmp.system.dto;

import com.wmmp.system.entity.SysDept;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/** 部门树形 VO */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptTreeVO extends SysDept {
    private List<DeptTreeVO> children;
}
