package com.wmmp.common;

import com.wmmp.common.result.PageResult;
import com.wmmp.common.result.R;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("R / PageResult 单元测试")
class ResultTest {
    @Test
    @DisplayName("R.ok() 返回 code=200")
    void ok_shouldReturn200() {
        R<String> r = R.ok("hello");
        assertThat(r.getCode()).isEqualTo(200);
        assertThat(r.getData()).isEqualTo("hello");
    }

    @Test
    @DisplayName("R.fail() 返回非200状态码")
    void fail_shouldReturnNon200() {
        R<Void> r = R.fail("error");
        assertThat(r.getCode()).isNotEqualTo(200);
    }

    @Test
    @DisplayName("PageResult.of() 正确封装分页数据")
    void pageResult_shouldWrapCorrectly() {
        List<String> items = List.of("a", "b", "c");
        PageResult<String> pr = PageResult.of(100L, items);
        assertThat(pr.getTotal()).isEqualTo(100L);
        assertThat(pr.getList()).hasSize(3);
    }
}
