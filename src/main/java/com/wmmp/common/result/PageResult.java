package com.wmmp.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import java.util.List;

/** 分页响应结果 */
@Data
public class PageResult<T> {
    private long total;
    private List<T> list;

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setList(page.getRecords());
        return result;
    }
}
