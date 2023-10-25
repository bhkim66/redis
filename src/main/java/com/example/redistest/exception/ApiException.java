package com.example.redistest.exception;

import com.example.redistest.entity.ExceptionEnum;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException{
    private ExceptionEnum error;

    public ApiException(ExceptionEnum e) {
        super(e.getErrorMessage());
        this.error = e;
    }
}
