package com.leafone.module.controller;

import com.leafone.common.response.R;
import com.leafone.module.model.Module;
import com.leafone.module.service.ModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "版块管理", description = "论坛版块相关接口")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @Operation(summary = "获取版块列表", description = "返回所有已启用的版块，按排序顺序排列")
    @GetMapping("/modules")
    public R<List<Module>> moduleList() {
        return R.ok(moduleService.moduleList());
    }
}
