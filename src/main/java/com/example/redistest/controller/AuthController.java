package com.example.redistest.controller;

import com.example.redistest.common.ConstDef;
import com.example.redistest.config.JwtTokenProvider;
import com.example.redistest.dto.UserRequestDto;
import com.example.redistest.entity.TokenInfo;
import com.example.redistest.exception.ApiException;
import com.example.redistest.util.Response;
import com.example.redistest.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.example.redistest.entity.ExceptionEnum.REDIS_USER_NOT_EXIST;
import static com.example.redistest.entity.ExceptionEnum.USER_LOGIN_DUPLICATION;

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
            return response.failed("계정 정보가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED);
        }
        log.info("authentication : {} " , authentication);
        if(checkRedis(login)) {
            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication.getName());
            log.info("tokenInfo : {} " , tokenInfo);

            jwtTokenProvider.insertRedis(tokenInfo, login.getConnectChannel());
            return response.success(tokenInfo, "로그인에 성공했습니다.", HttpStatus.OK);
        }
        return response.failed("오류 발생. 관리자에게 문의바랍니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/reissue-access")
    public ResponseEntity<?> reissueToken(HttpServletRequest req) {
        String refreshToken = jwtTokenProvider.resolveToken(req, ConstDef.REFRESH_AUTHORIZATION_HEADER);
        String connectChannel = req.getParameter("connectChannel");
        log.info("reissu token : {} ", refreshToken);
        String userId = jwtTokenProvider.getUserIdFromJWT(refreshToken);
        String redisUserKey = connectChannel + ConstDef.REDIS_KEY_PREFIX + userId;
        log.info("redisUserKey : {}", redisUserKey);

        String storedRefreshToken = (String) redisTemplate.opsForHash().get(redisUserKey, "refreshToken");

        if (StringUtil.isEmpty(storedRefreshToken)) {
            //RTK 만료 및 비정상일 경우 : 다시 로그인 필요
            SecurityContextHolder.clearContext();
            throw new ApiException(REDIS_USER_NOT_EXIST);
        }

        if (!jwtTokenProvider.matchRefreshToken(refreshToken, storedRefreshToken)) {
            SecurityContextHolder.clearContext();
            throw new ApiException(REDIS_USER_NOT_EXIST);
        }

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            //ATK, RTK 재발급
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(userId);
            Authentication authentication = jwtTokenProvider.getAuthentication(tokenInfo.getAccessToken());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            redisTemplate.delete(redisUserKey);
            jwtTokenProvider.insertRedis(tokenInfo, connectChannel);
            return response.success(tokenInfo, "토큰 재발급", HttpStatus.OK);
        }
        return response.failed("오류 발생. 관리자에게 문의바랍니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/token-test")
    public ResponseEntity<?> test() {
       return response.success("토큰 인증 성공했습니다.", HttpStatus.OK);
    }

    private boolean checkRedis(UserRequestDto.Login login) {
        log.info("checkRedis()");
        log.info("login {} " , login);
        String userKey = login.getConnectChannel() + ConstDef.REDIS_KEY_PREFIX + login.getUserId();
        String loginUser = (String)redisTemplate.opsForHash().get(userKey, "userId" );
        String loginUserIp = (String)redisTemplate.opsForHash().get(userKey, "userIp" );
        log.info("loginUser {} " , loginUser);
        // 유저가 있는거 까지 확인 사후처리 요망
        if(!StringUtil.isEmpty(loginUser)) {
            throw new ApiException(USER_LOGIN_DUPLICATION);
        }
        return true;
    }

}
