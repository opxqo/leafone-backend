package com.leafone.module.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leafone.module.mapper.ModuleMapper;
import com.leafone.module.model.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleMapper moduleMapper;

    public List<Module> moduleList() {
        LambdaQueryWrapper<Module> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Module::getEnabled, 1).orderByAsc(Module::getSortOrder);
        return moduleMapper.selectList(wrapper);
    }
}
