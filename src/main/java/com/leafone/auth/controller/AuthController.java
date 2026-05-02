package com.leafone.auth.controller;

import com.leafone.auth.service.AuthService;
import com.leafone.auth.service.dto.*;
import com.leafone.common.response.R;
import com.leafone.common.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理", description = "用户登录、注册、注销等接口")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "账号密码登录", description = "支持学号或手机号登录，返回 accessToken 和 refreshToken")
    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(authService.login(request));
    }

    @Operation(summary = "微信小程序登录", description = "通过微信 code 换取登录凭证")
    @PostMapping("/wechat-login")
    public R<LoginResponse> wechatLogin(@Valid @RequestBody WechatLoginRequest request) {
        return R.ok(authService.wechatLogin(request));
    }

    @Operation(summary = "学生注册", description = "学生账号注册，需填写姓名、学号、学院等信息")
    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return R.ok();
    }

    @Operation(summary = "退出登录", description = "注销当前用户，清除 Token")
    @PostMapping("/logout")
    public R<Void> logout(@AuthenticationPrincipal Long userId) {
        authService.logout(userId);
        return R.ok();
    }

    @Operation(summary = "学生认证", description = "提交学生信息进行认证，需登录")
    @PostMapping("/verify-student")
    public R<Void> verifyStudent(@Valid @RequestBody StudentVerificationRequest request,
                                 @AuthenticationPrincipal Long userId) {
        authService.verifyStudent(request, userId);
        return R.ok();
    }

    @Operation(summary = "刷新令牌", description = "使用 refreshToken 换取新的 accessToken")
    @PostMapping("/refresh")
    public R<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return R.ok(authService.refreshToken(request.getRefreshToken()));
    }
}
