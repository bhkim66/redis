package com.example.redistest.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class JwtTokenException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private HttpStatus statusCode;
	private String responseMessage;
	private Map<String, Object> error;
	
	public JwtTokenException(HttpStatus statusCode, String responseMessage, Map<String, Object> error) {
    	this.statusCode = statusCode;
    	this.responseMessage = responseMessage;
    	this.error = error;
    }

    
    public JwtTokenException(HttpStatus statusCode, String responseMessage, String error) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	resultMap.put("errorMassage", error);
    	
    	this.statusCode = statusCode;
    	this.responseMessage = responseMessage;
    	this.error = resultMap;
    }
    
    public JwtTokenException(HttpStatus statusCode, String responseMessage, int error) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	resultMap.put("errorCode", error);
    	
    	this.statusCode = statusCode;
    	this.responseMessage = responseMessage;
    	this.error = resultMap;
    }
}
