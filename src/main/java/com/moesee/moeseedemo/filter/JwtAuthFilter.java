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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final Set<String> EXCLUDED_URIS = Set.of(
            "/api/auth/login",
            "/api/auth/code",
            "/api/auth/mblogin",
            "/api/auth/setPassword",
            "/api/register"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String requestUri = request.getRequestURI();
        return EXCLUDED_URIS.contains(requestUri);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            String token = authHeader.substring(7);
            try {
                //验证JWT
                Claims claims = JWTUtils.parseToken(token);
                String userUid = claims.getSubject();
                //验证Redis中是否存在令牌
                String redisKey = "cache:auth:token:" + userUid;
                String redisToken = stringRedisTemplate.opsForValue().get(redisKey);
                if (redisToken != null && redisToken.equals(token)) {
                    // 将用户信息存入上下文，供后续使用
                    request.setAttribute("userUid", userUid);
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("令牌已过期");
                return;
            } catch (JwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("令牌无效");
                return;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("未授权");
    }
}

