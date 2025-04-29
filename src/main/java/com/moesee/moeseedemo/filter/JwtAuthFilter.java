package com.moesee.moeseedemo.filter;

import com.moesee.moeseedemo.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 使用 AntPathMatcher 以支持通配符匹配
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 白名单
    private static final Set<String> EXCLUDED_URIS = Set.of(
            "/pay",
            "/api/auth/login",
            "/api/auth/code",
            "/api/auth/mblogin",
            "/api/auth/setPassword",
            "/api/register",
            "/api/seckill/addVoucher",
            "/api/recommend",
            "/api/firstrecommend",
            "/api/recommendusers",
            "/api/seckill/order/*" // 支持通配符路径
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        // 遍历白名单进行通配符匹配
        return EXCLUDED_URIS.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return; // 放行预检请求
        }
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去除 "Bearer " 前缀
            try {
                // 验证 JWT
                Claims claims = JWTUtils.parseToken(token);
                String userUid = claims.getSubject();

                // 验证 Redis 中是否存在令牌
                String redisKey = "cache:auth:token:" + userUid;
                String redisToken = stringRedisTemplate.opsForValue().get(redisKey);

                if (redisToken != null && redisToken.equals(token)) {
                    // 将用户信息存入请求上下文
                    request.setAttribute("userUid", userUid);
                    filterChain.doFilter(request, response);
                    return; // 验证成功，放行
                } else {
                    // Redis 中不存在或令牌不匹配
                    respondWithUnauthorized(response, "令牌无效或已过期");
                    return;
                }
            } catch (ExpiredJwtException e) {
                respondWithUnauthorized(response, "令牌已过期");
                return;
            } catch (JwtException e) {
                respondWithUnauthorized(response, "令牌无效");
                return;
            }
        }

        // 未提供 Authorization Header
        respondWithUnauthorized(response, "未授权");
    }

    // 集中处理错误响应
    private void respondWithUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
