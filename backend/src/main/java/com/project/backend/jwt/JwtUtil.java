package com.project.backend.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.project.backend.config.RedisConfig;
import com.project.backend.dto.TokenDto;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.project.backend.entity.UserRoleEnum;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;

@Log
@Component
public class JwtUtil {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
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

    // header 토큰을 가져오기
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // AccessToken, RefreshToken 생성
    public TokenDto createToken(String userEmail, UserRoleEnum role) {
        log.info("token creating");
        // AccessToken
        String accessToken = createAllToken(userEmail, ACCESS_TOKEN, role);
        log.info("accessToken: " + accessToken);

        // RefreshToken
        String refreshToken = createAllToken(userEmail, REFRESH_TOKEN, role);
        log.info("refreshToken : " + refreshToken);
        redisTemplate.opsForValue().set("refreshToken", refreshToken);

        return new TokenDto(accessToken,refreshToken);
    }

    public String createAllToken(String userEmail, String token, UserRoleEnum role) {
        log.info("allToken creating");
        Date date = new Date();
        long time = token.equals(ACCESS_TOKEN) ? ACCESS_TOKEN_TIME : REFRESH_TOKEN_TIME;

        return BEARER_PREFIX + Jwts.builder().setSubject(userEmail).setExpiration(new Date(date.getTime() + time)).claim(AUTHORIZATION_KEY, role).setIssuedAt(date).signWith(key, signatureAlgorithm).compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
            // Accesstoken 재발급 후, 다시 인증요청 보내기
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
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
}
