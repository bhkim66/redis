package com.example.redistest.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponseResult<T> {

    private boolean success;
    private T data;
    private ApiError error;

    @Builder
    public ApiResponseResult(boolean success, T data, ApiError error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponseResult<T> success(T data) {
        return ApiResponseResult.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponseResult<T> failure(String errorCode, String message) {
        return ApiResponseResult.<T>builder()
                .success(false)
                .error(ApiError.builder()
                        .errorCode(errorCode)
                        .message(message)
                        .build())
                .build();
    }

    public static <T> ApiResponseResult<T> failure(ExceptionEnum ex) {
        return ApiResponseResult.<T>builder()
                .success(false)
                .error(ApiError.builder()
                        .errorCode(ex.getErrorCode())
                        .message(ex.getErrorMessage())
                        .build())
                .build();
    }

}
