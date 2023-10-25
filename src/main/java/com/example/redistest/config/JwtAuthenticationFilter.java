package com.example.redistest.config;

import com.example.redistest.common.ConstDef;
import com.example.redistest.exception.ApiException;
import com.example.redistest.exception.JwtTokenException;
import com.example.redistest.util.StringUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.redistest.entity.ExceptionEnum.USER_LOGIN_DUPLICATION;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_TYPE = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        log.info("INTO doFilter");
        // 1. Request Header 에서 JWT 토큰 추출
        String token = jwtTokenProvider.resolveToken(request, ConstDef.ACCESS_AUTHORIZATION_HEADER);
        String connectChannel = request.getParameter("connectChannel");
        String userId = request.getParameter("userId");
        log.info("token {} " , token);
        log.info("connectChannel {} " , connectChannel);

        // Redis 에 해당 accessToken logout 여부 확인
        // 2. validateToken 으로 토큰 유효성 검사
        try {
            if(token == null) {
                // Redis 에 해당 계정정보 존재 여부 확인
                log.info("connectChannel {} " , connectChannel);
                String loginUser = (String)redisTemplate.opsForHash().get(connectChannel + ConstDef.REDIS_KEY_PREFIX + userId, "userId" );
                String loginUserIp = (String)redisTemplate.opsForHash().get(connectChannel + ConstDef.REDIS_KEY_PREFIX + userId, "userIp" );
                log.info("loginUser {} " , loginUser);
                // 유저가 있는거 까지 확인 사후처리 요망
                if(!StringUtil.isEmpty(loginUser)) {
                    throw new ApiException(USER_LOGIN_DUPLICATION);
                }
            }
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch(JwtException e) {
            throw new JwtException("Invaild Token");
        } catch (RedisConnectionFailureException e) {
            SecurityContextHolder.clearContext();
            throw new RedisConnectionFailureException("bad error");
        }
        filterChain.doFilter(request, response);
    }
}
