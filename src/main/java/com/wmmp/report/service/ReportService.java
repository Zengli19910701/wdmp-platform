package com.wmmp.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wmmp.common.exception.BizException;
import com.wmmp.report.entity.SysReport;
import com.wmmp.report.mapper.SysReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.*;

/** 报表服务 */
@Service
@RequiredArgsConstructor
public class ReportService {
    private final SysReportMapper reportMapper;
    private final JdbcTemplate jdbcTemplate;

    public IPage<SysReport> page(int pageNum, int pageSize, String reportName) {
        return reportMapper.selectPage(new Page<>(pageNum, pageSize),
            new LambdaQueryWrapper<SysReport>()
                .like(StringUtils.hasText(reportName), SysReport::getReportName, reportName)
                .orderByDesc(SysReport::getCreateTime));
    }

    public SysReport getById(Long id) {
        SysReport report = reportMapper.selectById(id);
        if (report == null) throw new BizException("报表不存在");
        return report;
    }

    public void save(SysReport report) { reportMapper.insert(report); }

    public void update(SysReport report) {
        if (reportMapper.selectById(report.getId()) == null) throw new BizException("报表不存在");
        reportMapper.updateById(report);
    }

    public void delete(Long id) { reportMapper.deleteById(id); }

    /** 执行报表 SQL 查询（仅允许 SELECT） */
    public List<Map<String, Object>> queryData(Long reportId) {
        SysReport report = getById(reportId);
        if (!StringUtils.hasText(report.getDataSql())) throw new BizException("该报表未配置数据SQL");
        String sql = report.getDataSql().trim();
        if (!sql.toUpperCase().startsWith("SELECT")) throw new BizException("仅支持 SELECT 查询语句");
        return jdbcTemplate.queryForList(sql);
    }
}
