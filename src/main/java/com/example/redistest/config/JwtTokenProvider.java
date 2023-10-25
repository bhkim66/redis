package com.example.redistest.config;

import com.example.redistest.common.ConstDef;
import com.example.redistest.entity.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
@Component
public class JwtTokenProvider {
    public static final long now = (new Date()).getTime();
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 5 * 1000L;              // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 14 * 24 * 60 * 60 * 1000L;    // 14일

    private final Key key;

    @Autowired
    RedisTemplate redisTemplate;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public TokenInfo generateToken(Authentication authentication) {
         log.info("INTO generateToken");
         log.info("authentication {} " , authentication);
        // 권한 가져오기@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        String authorities = "M";

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .userId(authentication.getName())
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .rtkExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String token) {
        log.info("INTO getAuthentication");
        // 토큰 복호화
        Claims claims = parseClaims(token);

        if (claims.get(AUTHORITIES_KEY) == null || "".equals(claims.get(AUTHORITIES_KEY))) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }
        log.info("claims {} " , claims);
        log.info("accessToken {} " , token);
        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        log.info("authorities {} " , authorities);
        log.info("claims.getsubject {} " , claims.getSubject());
        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token){
        log.info("INTO validateToken");
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    // Request Header 에서 토큰 정보 추출
    public String resolveToken(HttpServletRequest request, String tokenType) {
        log.info("INTO resolveToken");
        String bearerToken = request.getHeader(tokenType);
        log.info("bearerToken : {} " , bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    public String getUserIdFromJWT(String token) {
		if(token == null || !validateToken(token)) {
			return "";
		}
		String userId = null;
        Claims claims = parseClaims(token);
        if(claims != null) {
            userId = (String)claims.get("sub");
        }
        return userId;
	}

//    public void validateRefreshToken(String token){
//        try {
//            // 검증
//            String userId = (String) redisTemplate.opsForHash().get(ConstDef.REDIS_KEY_PREFIX, "userId");
//            log.info("userId : {} ", userId);
//            //refresh 토큰의 만료시간이 지나지 않았을 경우, 새로운 access 토큰을 생성합니다.
//            if (!"".equals(userId)) {
//                insertRedis(recreationAccessToken(userId));
//            }
//        } catch (Exception e) {
//            //refresh 토큰이 만료되었을 경우, 로그인이 필요합니다.
//        }
//    }

    public TokenInfo recreationAccessToken(String userId){
        log.info("recreationAccessToken()");
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        // 권한가져오기 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        String authorities = "M";

        //Access Token
        String accessToken = Jwts.builder()
                .setSubject(userId) // 정보 저장
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 재생성
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        log.info("accessToken : {} ", accessToken);
        log.info("refreshToken : {} ", refreshToken);
        return TokenInfo.builder()
            .userId(userId)
            .grantType(BEARER_TYPE)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .rtkExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
            .build();
    }
    public void insertRedis(TokenInfo tokenInfo, String connectChannel) {
        log.info("insertRedis()");
        Map<String, Object> map = new HashMap<>();
        Date rtkExpiredDate = new Date(now + tokenInfo.getRtkExpirationTime());

        map.put("userId", tokenInfo.getUserId());
        map.put("refreshToken", tokenInfo.getRefreshToken());
        map.put("rtkExpirationTime", tokenInfo.getRtkExpirationTime().toString()) ;
        map.put("rtkExpirationDate", rtkExpiredDate.toString());
        map.put("userIp", "123.123.123.123");

        String userKey = connectChannel + ConstDef.REDIS_KEY_PREFIX + tokenInfo.getUserId();
        redisTemplate.opsForHash().putAll(userKey , map);
        redisTemplate.expire(userKey , tokenInfo.getRtkExpirationTime(), MILLISECONDS);
    }
}
