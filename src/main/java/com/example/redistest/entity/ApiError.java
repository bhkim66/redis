package com.example.redistest.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiError {
    private String message;
    private String errorCode;

    @Builder
    public ApiError(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

}
