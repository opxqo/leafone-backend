package com.leafone.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AccessLogFilter implements Filter {

    private static final String[] IGNORE_PATHS = {"/actuator", "/favicon.ico"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        for (String ignore : IGNORE_PATHS) {
            if (uri.startsWith(ignore)) {
                chain.doFilter(request, response);
                return;
            }
        }

        long start = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            long cost = System.currentTimeMillis() - start;
            String method = req.getMethod();
            int status = resp.getStatus();
            String query = req.getQueryString();
            String remote = req.getRemoteAddr();
            String path = query != null ? uri + "?" + query : uri;

            log.info("{} {} {} {}ms {}", method, status, path, cost, remote);
        }
    }
}
