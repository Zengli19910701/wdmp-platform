package com.wmmp.org.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wmmp.common.exception.BizException;
import com.wmmp.org.entity.SysOrg;
import com.wmmp.org.mapper.SysOrgMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;

/** 组织机构服务 */
@Service
@RequiredArgsConstructor
public class OrgService {
    private final SysOrgMapper orgMapper;

    public List<OrgTreeVO> listTree() {
        List<SysOrg> orgs = orgMapper.selectList(
            new LambdaQueryWrapper<SysOrg>().orderByAsc(SysOrg::getParentId, SysOrg::getOrderNum));
        return buildTree(orgs, 0L);
    }

    public void save(SysOrg org) { orgMapper.insert(org); }

    public void update(SysOrg org) {
        if (orgMapper.selectById(org.getId()) == null) throw new BizException("组织不存在");
        orgMapper.updateById(org);
    }

    public void delete(Long id) {
        long childCount = orgMapper.selectCount(new LambdaQueryWrapper<SysOrg>().eq(SysOrg::getParentId, id));
        if (childCount > 0) throw new BizException("存在子组织，不能删除");
        orgMapper.deleteById(id);
    }

    private List<OrgTreeVO> buildTree(List<SysOrg> list, Long parentId) {
        return list.stream().filter(o -> parentId.equals(o.getParentId())).map(o -> {
            OrgTreeVO vo = new OrgTreeVO();
            BeanUtils.copyProperties(o, vo);
            List<OrgTreeVO> children = buildTree(list, o.getId());
            if (!children.isEmpty()) vo.setChildren(children);
            return vo;
        }).toList();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class OrgTreeVO extends SysOrg {
        private List<OrgTreeVO> children;
    }
}
