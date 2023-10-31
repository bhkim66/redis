package com.example.redistest.config;

import com.example.redistest.entity.ApiResponseResult;
import com.example.redistest.exception.ApiException;
import com.example.redistest.util.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        try {
            filterChain.doFilter(request, response); // go to 'JwtAuthenticationFilter'
        } catch (ApiException e){
            log.error("ApiException!!!" , e.getMessage());
            setErrorResponse(HttpStatus.CONFLICT, response, e);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse res, ApiException e) throws IOException {
        log.info("setErrorResponse()");
        res.setStatus(status.value());
        res.setContentType("application/json; charset=UTF-8");

        ApiResponseResult<String> responseResult = ApiResponseResult.failure(e.getError().getErrorCode(), e.getMessage());
        ResponseEntity<ApiResponseResult<String>> response = new ResponseEntity<>(responseResult, HttpStatus.CONFLICT);
        String result = objectMapper.writeValueAsString(response);

        res.getWriter().write(String.valueOf(result));
    }
}