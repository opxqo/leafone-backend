package com.leafone.admin.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "Redis缓存状态")
public class CacheStatusResponse {

    @Schema(description = "Redis是否可用", example = "true")
    private boolean available;

    @Schema(description = "Redis服务器信息")
    private RedisInfo info;

    @Schema(description = "电费缓存条目列表")
    private List<CacheEntry> dormPowerCaches;

    @Schema(description = "认证缓存条目列表")
    private List<CacheEntry> authCaches;

    @Data
    @Schema(description = "Redis服务器信息")
    public static class RedisInfo {
        @Schema(description = "Redis版本", example = "7.0.0")
        private String version;
        @Schema(description = "已用内存", example = "1.5M")
        private String usedMemory;
        @Schema(description = "连接的客户端数", example = "5")
        private String connectedClients;
        @Schema(description = "运行时间（天）", example = "30")
        private String uptimeDays;
        @Schema(description = "key总数", example = "128")
        private String totalKeys;
    }

    @Data
    @Schema(description = "缓存条目")
    public static class CacheEntry {
        @Schema(description = "缓存key", example = "dorm_power:balance:南1-103")
        private String key;
        @Schema(description = "剩余TTL（秒）", example = "1523")
        private Long ttlSeconds;
        @Schema(description = "缓存值（脱敏）", example = "5.23,0.0")
        private String value;
    }
}
