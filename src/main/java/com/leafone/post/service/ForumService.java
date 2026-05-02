package com.leafone.post.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leafone.module.mapper.ModuleMapper;
import com.leafone.module.model.Module;
import com.leafone.post.mapper.TopicMapper;
import com.leafone.post.model.Topic;
import com.leafone.post.service.dto.ForumOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final ModuleMapper moduleMapper;
    private final TopicMapper topicMapper;

    public ForumOverviewResponse forumOverview() {
        List<Module> modules = moduleMapper.selectList(
                new LambdaQueryWrapper<Module>()
                        .eq(Module::getEnabled, 1)
                        .orderByAsc(Module::getSortOrder));

        List<Topic> hotTopics = topicMapper.selectList(
                new LambdaQueryWrapper<Topic>()
                        .eq(Topic::getEnabled, 1)
                        .orderByDesc(Topic::getHeatScore)
                        .last("LIMIT 20"));

        ForumOverviewResponse resp = new ForumOverviewResponse();
        resp.setModules(modules);
        resp.setHotTopics(hotTopics);
        return resp;
    }
}
