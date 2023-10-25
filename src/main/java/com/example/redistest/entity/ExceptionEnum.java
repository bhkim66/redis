package com.example.redistest.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionEnum {
    INVALID_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "E001", "올바르지 않은 파라미터입니다."),
    INVALID_FORMAT_ERROR(HttpStatus.BAD_REQUEST,"E002", "올바르지 않은 포맷입니다."),
    INVALID_TYPE_ERROR(HttpStatus.BAD_REQUEST, "E003", "올바르지 않은 타입입니다."),
    ILLEGAL_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "E004", "필수 파라미터가 없습니다"),

    //HttpStatus.NOT_FOUND
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "E030", "데이터를 찾을수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E041", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),

    USER_LOGIN_DUPLICATION(HttpStatus.UNAUTHORIZED, "E091", "이미 로그인 되어있는 계정입니다."),
    ;



    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

}
