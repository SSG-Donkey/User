package com.project.backend.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.project.backend.dto.TokenDto;
import com.project.backend.entity.User;
import com.project.backend.repository.UserRepository;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.project.backend.entity.UserRoleEnum;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

@Log
@Component
public class JwtUtil {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserRepository UserRepository;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    // private static final long TOKEN_TIME = 60 * 60 * 1000L;

    public static final String ACCESS_TOKEN = "Access_Token";
    public static final String REFRESH_TOKEN = "Refresh_Token";
    public static final long ACCESS_TOKEN_TIME = 30 * 60 * 1000L; // AccessToken Time 30 min
    public static final long REFRESH_TOKEN_TIME = 24 * 60 * 60 * 1000L; // RefreshToken Time 1 day

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // AccessToken, RefreshToken 생성 요청
    public TokenDto createToken(String userEmail, UserRoleEnum role) {
        log.info("Creating token for user: " + userEmail);

        // AccessToken 생성
        String accessToken = createAllToken(userEmail, ACCESS_TOKEN, role);
        // RefreshToken 생성( accessToken 을 key 에 포함시켜 찾기 쉽게 저장 )
        String refreshToken = createAllToken(userEmail, REFRESH_TOKEN, role);

        log.info("AccessToken: " + accessToken);
        log.info("RefreshToken: " + refreshToken);


        redisTemplate.opsForValue().set("refreshToken: " + userEmail, refreshToken, JwtUtil.REFRESH_TOKEN_TIME, TimeUnit.SECONDS);
        return new TokenDto(accessToken, refreshToken);
    }

    // token 생성
    public String createAllToken(String userEmail, String token, UserRoleEnum role) {
        Date date = new Date();
        long time = token.equals(ACCESS_TOKEN) ? ACCESS_TOKEN_TIME : REFRESH_TOKEN_TIME;

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(userEmail)
                        .setExpiration(new Date(date.getTime() + time))
                        .claim(AUTHORIZATION_KEY, role)
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    // 토큰의 남은 유효시간을 반환
    public long getRemainingTime(String token) {
        long expirationTime = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().getTime();
        long currentTime = new Date().getTime();
        return expirationTime - currentTime;
    }

    // Header에 있는 AccessToken 가져오기
    public String resolveToken(HttpServletRequest request) {
        // cookie 값 가져오기
        Cookie[] cookies = request.getCookies();
        log.info("cookie들: "+cookies);
        // 쿠키가 존재할 경우
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // AccessToken이라는 이름의 쿠키를 가져와서 accessToken에 넣음
                if (cookie.getName().equals("AccessToken")) {
                    String accessToken = cookie.getValue();
                    log.info("Bearer Token: " + accessToken);
                    if (StringUtils.hasText(accessToken) && accessToken.startsWith(BEARER_PREFIX)) {
                        return accessToken.substring(7);
                    }
                }
            }
        }
        return null;
    }


    // 헤더가 없는 토큰 추출
    public String resolveToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }
        return null;
    }

    // 토큰에서 사용자 정보 가져오기
    public String getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    // 인증 객체 생성
    public Authentication createAuthentication(String username) {
        UserDetailsService userDetailsService = getUserDetailsService();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private UserDetailsService getUserDetailsService() {
        return applicationContext.getBean(UserDetailsService.class);
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }
}
