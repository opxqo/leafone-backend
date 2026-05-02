package com.leafone.search.controller;

import com.leafone.common.response.PageResult;
import com.leafone.common.response.R;
import com.leafone.post.model.Post;
import com.leafone.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "搜索", description = "帖子搜索接口")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "搜索帖子", description = "根据关键词搜索帖子标题、摘要和正文")
    @GetMapping("/search")
    public R<PageResult<Post>> search(
            @Parameter(description = "搜索关键词") @RequestParam(defaultValue = "") String keyword,
            @Parameter(description = "搜索类型（all/post）") @RequestParam(defaultValue = "all") String type,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize) {
        pageSize = PageResult.capPageSize(pageSize);
        return R.ok(searchService.search(keyword, type, page, pageSize));
    }
}
