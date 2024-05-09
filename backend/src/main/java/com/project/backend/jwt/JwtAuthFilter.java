package com.project.backend.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.backend.dto.ResponseMsgDto;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    @Autowired
    final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("TokenFiltering ----");

        String token = jwtUtil.resolveToken(request);

        log.info("resolveToken : "+token);
        if (token != null) {
            // Access 토큰 유효 시, security context에 인증 정보 저장
            if (jwtUtil.validateToken(token)) {
                // Redis에 해당 accessToken logout 여부를 확인
                String isLogout = (String) redisTemplate.opsForValue().get("BL:" + token);
                log.info("로그인 유무() :  "+isLogout);
                // 로그아웃이 없는(되어 있지 않은) 경우 해당 토큰은 정상적으로 작동하기
                if (ObjectUtils.isEmpty(isLogout)) {
                    setAuthentication(jwtUtil.getUserInfoFromToken(token));
                }
            } else {
                jwtExceptionHandler(response, "Access Token Expired", HttpStatus.FORBIDDEN.value());
                return;
            }
            // Refresh Token을 통한 Access Token 재발급을 Http 요청에 의해 따로 처리하도록 변경
        }
        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(username);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    public void jwtExceptionHandler(HttpServletResponse response, String msg, int statusCode) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(ResponseMsgDto.setFail(statusCode, msg));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
