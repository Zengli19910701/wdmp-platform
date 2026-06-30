package com.wmmp.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wmmp.common.exception.BizException;
import com.wmmp.system.dto.DeptTreeVO;
import com.wmmp.system.entity.SysDept;
import com.wmmp.system.mapper.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;

/** 部门服务 */
@Service
@RequiredArgsConstructor
public class SysDeptService {
    private final SysDeptMapper deptMapper;

    public List<DeptTreeVO> listTree() {
        List<SysDept> depts = deptMapper.selectList(
            new LambdaQueryWrapper<SysDept>().orderByAsc(SysDept::getParentId, SysDept::getOrderNum));
        return buildTree(depts, 0L);
    }

    public void save(SysDept dept) { deptMapper.insert(dept); }

    public void update(SysDept dept) {
        if (deptMapper.selectById(dept.getId()) == null) throw new BizException("部门不存在");
        deptMapper.updateById(dept);
    }

    public void delete(Long id) {
        long childCount = deptMapper.selectCount(new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, id));
        if (childCount > 0) throw new BizException("存在子部门，不能删除");
        deptMapper.deleteById(id);
    }

    private List<DeptTreeVO> buildTree(List<SysDept> list, Long parentId) {
        return list.stream().filter(d -> parentId.equals(d.getParentId())).map(d -> {
            DeptTreeVO vo = new DeptTreeVO();
            BeanUtils.copyProperties(d, vo);
            List<DeptTreeVO> children = buildTree(list, d.getId());
            if (!children.isEmpty()) vo.setChildren(children);
            return vo;
        }).toList();
    }
}
