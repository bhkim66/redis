package com.example.redistest.config;

import com.example.redistest.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증되지 않은 토큰");
    }
//	@Override
//	public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ae) throws IOException, ServletException {
//
//
//		String exception = (String) req.getAttribute("exception");
//		ErrorCode errorCode;
//
//		if(exception == null) {
//			errorCode = ErrorCode.UNAUTHORIZEDException;
//			setResponse(res, errorCode);
//			return;
//		}
//
//		if(exception.equals("ExpiredJwtException")) {
//			errorCode = ErrorCode.ExpiredJwtException;
//			setResponse(res, errorCode);
//			return;
//		}
//	}

//	@SuppressWarnings("unchecked")
//	private void setResponse(HttpServletResponse res, ErrorCode errorCode) throws IOException {
//		JSONObject json = new JSONObject();
//		res.setContentType("application/json;charset=UTF-8");
//		res.setCharacterEncoding("utf-8");
//		res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//		json.put("code", errorCode.getCode());
//		json.put("message", errorCode.getMessage());
//
//		res.getWriter().print(json);
//	}
}
