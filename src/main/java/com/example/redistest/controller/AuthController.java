package com.example.redistest.controller;

import com.example.redistest.common.ConstDef;
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

import javax.servlet.http.HttpServletRequest;

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

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDto.Login login, HttpServletRequest request) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        log.info("login : {} " , login);
        String connectChannel = request.getParameter("connectChannel");

        UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();



        log.info("authenticationToken : {} " , authenticationToken);

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
         Authentication authentication = null;

         try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            log.info("authentication : {} " , authentication);
            log.info("authentication getName : {} " , authentication.getName());
         } catch (BadCredentialsException e) {
             return response.success("실패 했습니다.", HttpStatus.NON_AUTHORITATIVE_INFORMATION);
         }
         log.info("authentication : {} " , authentication);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        log.info("tokenInfo : {} " , tokenInfo);
        log.info("connectChannel : {} " , connectChannel);

        jwtTokenProvider.insertRedis(tokenInfo, connectChannel);

        return response.success(tokenInfo, "로그인에 성공했습니다.", HttpStatus.OK);
    }

    @GetMapping("/reissue-access")
    public ResponseEntity<?> getAccessToken(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request, ConstDef.REFRESH_AUTHORIZATION_HEADER);
        log.info("jwtTokenProvider.validateToken(token) : {} ", jwtTokenProvider.validateToken(token));
        if (token != null && jwtTokenProvider.validateToken(token)) {
            //3. 저장된 refresh token 찾기
            TokenInfo tokenInfo = (TokenInfo) redisTemplate.opsForHash().entries(ConstDef.REDIS_KEY_PREFIX + jwtTokenProvider.getUserIdFromJWT(token));
            log.info("tokenInfo : {} ", tokenInfo);
//            if (refreshToken != null) {
//                //4. 최초 로그인한 ip 와 같은지 확인 (처리 방식에 따라 재발급을 하지 않거나 메일 등의 알림을 주는 방법이 있음)
//                String currentIpAddress = Helper.getClientIp(request);
//                if (refreshToken.getIp().equals(currentIpAddress)) {
//                    // 5. Redis 에 저장된 RefreshToken 정보를 기반으로 JWT Token 생성
//                    UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(refreshToken.getId(), refreshToken.getAuthorities());
//
//                    // 6. Redis RefreshToken update
//                    refreshTokenRedisRepository.save(RefreshToken.builder()
//                            .id(refreshToken.getId())
//                            .ip(currentIpAddress)
//                            .authorities(refreshToken.getAuthorities())
//                            .refreshToken(tokenInfo.getRefreshToken())
//                            .build());
//                    return response.success(tokenInfo);
//                }
//            }
        }
        return response.success("test!!", HttpStatus.OK);
    }

    @GetMapping("/token-test")
    public ResponseEntity<?> test() {
       return response.success("토큰 인증 성공했습니다.", HttpStatus.OK);
    }

}
