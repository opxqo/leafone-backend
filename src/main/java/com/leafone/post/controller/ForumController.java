package com.leafone.post.controller;

import com.leafone.common.response.R;
import com.leafone.post.service.ForumService;
import com.leafone.post.service.dto.ForumOverviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "论坛概览", description = "论坛首页概览信息，包含版块和热门话题")
@RestController
@RequestMapping("/api/v1/forum")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;

    @Operation(summary = "论坛概览", description = "获取论坛版块列表和热门话题")
    @GetMapping("/overview")
    public R<ForumOverviewResponse> forumOverview() {
        return R.ok(forumService.forumOverview());
    }
}
