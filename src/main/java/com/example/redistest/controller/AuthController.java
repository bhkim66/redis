package com.example.redistest.controller;

import com.example.redistest.config.JwtTokenProvider;
import com.example.redistest.dto.UserRequestDto;
import com.example.redistest.entity.TokenInfo;
import com.example.redistest.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    Response response;
    @Autowired
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    RedisTemplate redisTemplate;

    public static final long now = (new Date()).getTime();


    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDto.Login login) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        log.info("login : {} " , login);
        UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();

        log.info("authenticationToken : {} " , authenticationToken);

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
         Authentication authentication = null;

         try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            log.info("authentication : {} " , authentication);
         } catch (BadCredentialsException e) {
             return response.success("실패 했습니다.", HttpStatus.NON_AUTHORITATIVE_INFORMATION);
         }
         log.info("authentication : {} " , authentication);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        log.info("tokenInfo : {} " , tokenInfo);

        //RefreshToken Redis 저장

        Map<String, Object> map = new HashMap<>();
        Date RTKExpiredDate = new Date(now + tokenInfo.getRefreshTokenExpirationTime());

        map.put("userId", login.getMemId());
        map.put("RTK", tokenInfo.getRefreshToken());
        map.put("RTKExpirationTime", tokenInfo.getRefreshTokenExpirationTime().toString()) ;
        map.put("RTKExpirationDate", RTKExpiredDate.toString());
        map.put("userIp", "123.123.123.123");
        map.put("connectChannel", "M");
        log.info("map : {} " , map);

        String userKey = "User:" + login.getMemId();
        redisTemplate.opsForHash().putAll(userKey , map);
        redisTemplate.expire(userKey , tokenInfo.getRefreshTokenExpirationTime(), MILLISECONDS);


        return response.success(tokenInfo, "로그인에 성공했습니다.", HttpStatus.OK);
    }

    @GetMapping("/token-test")
    public ResponseEntity<?> test() {
       return response.success("토큰 인증 성공했습니다.", HttpStatus.OK);
    }

}
