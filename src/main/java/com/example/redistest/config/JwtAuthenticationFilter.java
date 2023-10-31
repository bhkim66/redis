package com.example.redistest.config;

import com.example.redistest.common.ConstDef;
import com.example.redistest.entity.TokenInfo;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        String atk = jwtTokenProvider.resolveToken(request, ConstDef.ACCESS_AUTHORIZATION_HEADER);
        String rtk = jwtTokenProvider.resolveToken(request, ConstDef.REFRESH_AUTHORIZATION_HEADER);
        log.info("atk : {} " , atk);
        // Redis 에 해당 accessToken logout 여부 확인
        // 2. validateToken 으로 토큰 유효성 검사
        try {
//            if(token == null) {
//                // Redis 에 해당 계정정보 존재 여부 확인
//                log.info("connectChannel {} " , connectChannel);
//                String loginUser = (String)redisTemplate.opsForHash().get(connectChannel + ConstDef.REDIS_KEY_PREFIX + userId, "userId" );
//                String loginUserIp = (String)redisTemplate.opsForHash().get(connectChannel + ConstDef.REDIS_KEY_PREFIX + userId, "userIp" );
//                log.info("loginUser {} " , loginUser);
//                // 유저가 있는거 까지 확인 사후처리 요망
//                if(!StringUtil.isEmpty(loginUser)) {
//                    throw new ApiException(USER_LOGIN_DUPLICATION);
//                }
//            }
            if(atk != null && jwtTokenProvider.validateToken(atk)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(atk);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if(atk == null || !jwtTokenProvider.validateToken(atk)) {
                 SecurityContextHolder.clearContext();
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
