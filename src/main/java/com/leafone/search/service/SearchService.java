package com.leafone.search.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafone.common.response.PageResult;
import com.leafone.post.mapper.PostMapper;
import com.leafone.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PostMapper postMapper;

    public PageResult<Post> search(String keyword, String type, int page, int pageSize) {
        if (keyword.isBlank()) {
            return PageResult.of(List.of(), page, pageSize, 0);
        }

        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1)
               .and(w -> w.like(Post::getTitle, keyword)
                          .or().like(Post::getSummary, keyword)
                          .or().like(Post::getContent, keyword))
               .orderByDesc(Post::getPublishedAt);

        Page<Post> pageResult = postMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageResult.of(pageResult.getRecords(), page, pageSize, pageResult.getTotal());
    }
}
