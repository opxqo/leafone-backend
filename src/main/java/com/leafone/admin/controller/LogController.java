package com.leafone.admin.controller;

import com.leafone.common.exception.BizException;
import com.leafone.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Tag(name = "日志查看", description = "管理员在线查看系统运行日志")
@RestController
@RequestMapping("/api/v1/admin/logs")
@PreAuthorize("hasRole('ADMIN')")
public class LogController {

    @Value("${leafone.log.enabled:true}")
    private boolean logEndpointEnabled;

    @Value("${LOG_HOME:./logs}")
    private String logHome;

    @Operation(summary = "日志文件列表", description = "获取所有可用的日志文件")
    @GetMapping("/files")
    public R<List<Map<String, Object>>> listFiles() {
        checkEnabled();
        Path dir = Paths.get(logHome);
        if (!Files.exists(dir)) {
            return R.ok(List.of());
        }

        List<Map<String, Object>> files = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(p -> p.toString().endsWith(".log"))
                  .sorted(Comparator.comparing(p -> {
                      try { return Files.getLastModifiedTime(p); }
                      catch (IOException e) { return null; }
                  }, Comparator.reverseOrder()))
                  .forEach(p -> {
                      Map<String, Object> info = new LinkedHashMap<>();
                      info.put("name", p.getFileName().toString());
                      try {
                          info.put("size", Files.size(p));
                          info.put("lastModified", Files.getLastModifiedTime(p).toInstant().toString());
                      } catch (IOException ignored) {}
                      files.add(info);
                  });
        } catch (IOException e) {
            log.error("Failed to list log files", e);
        }
        return R.ok(files);
    }

    @Operation(summary = "查看日志内容", description = "读取指定日志文件的最后 N 行")
    @GetMapping("/files/{fileName}")
    public R<List<String>> readFile(
            @Parameter(description = "日志文件名") @PathVariable String fileName,
            @Parameter(description = "读取行数（默认200）") @RequestParam(defaultValue = "200") int lines) {
        checkEnabled();
        Path file = resolveLogFile(fileName);

        try (Stream<String> stream = Files.lines(file, StandardCharsets.UTF_8)) {
            Deque<String> buffer = new ArrayDeque<>(lines);
            stream.forEach(line -> {
                if (buffer.size() >= lines) buffer.pollFirst();
                buffer.addLast(line);
            });
            return R.ok(new ArrayList<>(buffer));
        } catch (IOException e) {
            throw new BizException(50000, "Failed to read log file: " + e.getMessage());
        }
    }

    @Operation(summary = "按关键词搜索日志", description = "在指定日志文件中搜索包含关键词的行")
    @GetMapping("/files/{fileName}/search")
    public R<List<String>> searchFile(
            @Parameter(description = "日志文件名") @PathVariable String fileName,
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "最大返回行数（默认100）") @RequestParam(defaultValue = "100") int limit) {
        checkEnabled();
        Path file = resolveLogFile(fileName);

        try {
            List<String> matched = new ArrayList<>();
            try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
                lines.filter(line -> line.contains(keyword))
                     .limit(limit)
                     .forEach(matched::add);
            }
            return R.ok(matched);
        } catch (IOException e) {
            throw new BizException(50000, "Failed to search log file: " + e.getMessage());
        }
    }

    private Path resolveLogFile(String fileName) {
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new BizException(40000, "Invalid file name");
        }
        Path file = Paths.get(logHome, fileName);
        if (!Files.exists(file) || !file.toString().endsWith(".log")) {
            throw new BizException(40400, "Log file not found");
        }
        return file;
    }

    private void checkEnabled() {
        if (!logEndpointEnabled) {
            throw new BizException(40300, "Log endpoint is disabled");
        }
    }
}
