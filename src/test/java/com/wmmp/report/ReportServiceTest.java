package com.wmmp.report;

import com.wmmp.common.exception.BizException;
import com.wmmp.report.entity.SysReport;
import com.wmmp.report.mapper.SysReportMapper;
import com.wmmp.report.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * ReportService 单元测试
 * 覆盖：非 SELECT 语句拦截、正常 SQL 查询、报表不存在
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService 单元测试")
class ReportServiceTest {
    @Mock private SysReportMapper reportMapper;
    @Mock private JdbcTemplate jdbcTemplate;
    @InjectMocks private ReportService reportService;

    @Test
    @DisplayName("queryData：非 SELECT 语句时抛出 BizException")
    void queryData_nonSelect_shouldThrow() {
        SysReport report = new SysReport(); report.setId(1L); report.setDataSql("DELETE FROM sys_user");
        given(reportMapper.selectById(1L)).willReturn(report);
        assertThatThrownBy(() -> reportService.queryData(1L))
                .isInstanceOf(BizException.class).hasMessageContaining("只允许 SELECT");
    }

    @Test
    @DisplayName("queryData：正常 SELECT 语句返回数据")
    void queryData_validSelect_shouldReturnData() {
        SysReport report = new SysReport(); report.setId(2L); report.setDataSql("SELECT id, username FROM sys_user");
        given(reportMapper.selectById(2L)).willReturn(report);
        given(jdbcTemplate.queryForList("SELECT id, username FROM sys_user"))
                .willReturn(List.of(Map.of("id", 1, "username", "admin")));
        List<Map<String, Object>> result = reportService.queryData(2L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).containsEntry("username", "admin");
    }

    @Test
    @DisplayName("queryData：报表不存在时抛出 BizException")
    void queryData_reportNotFound_shouldThrow() {
        given(reportMapper.selectById(999L)).willReturn(null);
        assertThatThrownBy(() -> reportService.queryData(999L))
                .isInstanceOf(BizException.class).hasMessageContaining("报表不存在");
    }
}
