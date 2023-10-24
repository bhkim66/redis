package com.example.redistest.config;

import com.example.redistest.exception.JwtTokenException;
import com.example.redistest.util.Response;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Autowired
    Response response;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        try {
            filterChain.doFilter(request, response); // go to 'JwtAuthenticationFilter'
        } catch (ExpiredJwtException e){
            //토큰의 유효기간 만료
            log.info("ExpiredJwtException!!");
            setErrorResponse(HttpStatus.UNAUTHORIZED, response);
        } catch (JwtException | IllegalArgumentException e){
            //유효하지 않은 토큰
            log.info("JwtException!!");
            setErrorResponse(HttpStatus.BAD_REQUEST, response);
        } catch (JwtTokenException e) {
            log.info("JwtTokenException!!");
            setErrorResponse(HttpStatus.UNAUTHORIZED, response);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse res) throws IOException {
        log.info("setErrorResponse()");
        res.setStatus(status.value());
        res.setContentType("application/json; charset=UTF-8");

        JwtTokenException jwtTokenException = new JwtTokenException(HttpStatus.UNAUTHORIZED, "Invaild Token", "error");

        res.getWriter().write(String.valueOf(jwtTokenException));
    }
}