package com.leafone.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "分页结果")
public class PageResult<T> {

    public static final int MAX_PAGE_SIZE = 100;

    @Schema(description = "数据列表")
    private List<T> items;
    @Schema(description = "当前页码", example = "1")
    private int page;
    @Schema(description = "每页数量", example = "20")
    private int pageSize;
    @Schema(description = "总记录数", example = "100")
    private long total;
    @Schema(description = "是否有下一页", example = "true")
    private boolean hasMore;

    public static <T> PageResult<T> of(List<T> items, int page, int pageSize, long total) {
        PageResult<T> result = new PageResult<>();
        result.setItems(items);
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setTotal(total);
        result.setHasMore((long) page * pageSize < total);
        return result;
    }

    public static int capPageSize(int pageSize) {
        return Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);
    }
}
