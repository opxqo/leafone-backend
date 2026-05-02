package com.leafone.post.service.dto;

import com.leafone.module.model.Module;
import com.leafone.post.model.Topic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "论坛概览响应")
public class ForumOverviewResponse {
    @Schema(description = "版块列表")
    private List<Module> modules;
    @Schema(description = "热门话题列表")
    private List<Topic> hotTopics;
}
