package com.leafone.profile.controller;

import com.leafone.common.util.AuthUtil;
import com.leafone.common.response.R;
import com.leafone.profile.service.DormPowerService;
import com.leafone.profile.service.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "寝室电费", description = "寝室电费余额和用电量查询")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DormPowerController {

    private final DormPowerService dormPowerService;

    @Operation(summary = "切换电费公开状态", description = "设置是否允许他人查看自己的电费信息")
    @PutMapping("/me/dorm/public")
    public R<Void> toggleDormPublic(
            @Parameter(description = "是否公开（true=公开, false=私密）") @RequestParam boolean isPublic,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        dormPowerService.toggleDormPublic(userId, isPublic);
        return R.ok();
    }

    @Operation(summary = "绑定寝室房间号", description = "绑定寝室房间号到个人资料，用于电费查询")
    @PostMapping("/me/dorm/bind")
    public R<Void> bindRoomName(
            @Valid @RequestBody DormPowerBindRequest request,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        dormPowerService.bindRoomName(userId, request.getRoomName(), request.getPassword());
        return R.ok();
    }

    @Operation(summary = "查询电费余额", description = "查询当前寝室的基本账户和补助账户余额")
    @GetMapping("/me/dorm/balance")
    public R<DormPowerBalanceResponse> getBalance(@AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        return R.ok(dormPowerService.getBalance(userId));
    }

    @Operation(summary = "查询月用电量", description = "查询最近6个月的月度用电量统计")
    @GetMapping("/me/dorm/monthly")
    public R<DormPowerMonthlyResponse> getMonthly(@AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        return R.ok(dormPowerService.getMonthly(userId));
    }

    @Operation(summary = "查询日用电量", description = "查询最近7天的每日用电量统计")
    @GetMapping("/me/dorm/daily")
    public R<DormPowerDailyResponse> getDaily(@AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        return R.ok(dormPowerService.getDaily(userId));
    }

    @Operation(summary = "查询72小时用电量", description = "查询最近72小时的每小时用电量统计")
    @GetMapping("/me/dorm/hourly")
    public R<DormPowerHourlyResponse> getHourly(@AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        return R.ok(dormPowerService.getHourly(userId));
    }

    // ==================== 查看他人电费（需对方公开） ====================

    @Operation(summary = "查看他人电费余额", description = "查看指定用户的电费余额，需对方已公开电费信息")
    @GetMapping("/users/{userId}/dorm/balance")
    public R<DormPowerBalanceResponse> getUserBalance(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return R.ok(dormPowerService.getPublicBalance(userId));
    }

    @Operation(summary = "查看他人月用电量", description = "查看指定用户的月用电量，需对方已公开电费信息")
    @GetMapping("/users/{userId}/dorm/monthly")
    public R<DormPowerMonthlyResponse> getUserMonthly(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return R.ok(dormPowerService.getPublicMonthly(userId));
    }

    @Operation(summary = "查看他人日用电量", description = "查看指定用户的日用电量，需对方已公开电费信息")
    @GetMapping("/users/{userId}/dorm/daily")
    public R<DormPowerDailyResponse> getUserDaily(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return R.ok(dormPowerService.getPublicDaily(userId));
    }

    @Operation(summary = "查看他人72小时用电量", description = "查看指定用户的72小时用电量，需对方已公开电费信息")
    @GetMapping("/users/{userId}/dorm/hourly")
    public R<DormPowerHourlyResponse> getUserHourly(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return R.ok(dormPowerService.getPublicHourly(userId));
    }
}
