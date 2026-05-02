package com.leafone.common.util;

import com.leafone.common.exception.BizException;

public final class AuthUtil {

    private AuthUtil() {}

    public static Long requireLogin(Long userId) {
        if (userId == null) {
            throw new BizException(40100, "请先登录");
        }
        return userId;
    }
}
