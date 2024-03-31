//package com.example.redistest.exception;
//
//import lombok.extern.slf4j.Slf4j;
//import net.kpnp.entity.ApiResponseResult;
//import net.kpnp.entity.ExceptionEnum;
//import org.springframework.core.NestedExceptionUtils;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionAdvice {
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponseResult> handleException(Exception e) {
//        log.error("[Exception] cause: {} , message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
//        ExceptionEnum internalServerError = ExceptionEnum.INTERNAL_SERVER_ERROR;
//        return ResponseEntity.status(internalServerError.getHttpStatus()).body(ApiResponseResult.failure(internalServerError));
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ApiResponseResult> handleApiException(ApiException e) {
//        log.error("[ApiException] cause: {} , message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
//        return ResponseEntity.status(e.getError().getHttpStatus()).body(ApiResponseResult.failure(e.getError()));
//    }
//
//    //메소드가 잘못되었거나 부적합한 인수를 전달했을 경우 -> 필수 파라미터 없을 때
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ApiResponseResult> handleIllegalArgumentException(IllegalArgumentException e){
//        log.error("[IllegalArgumentException] cause: {} , message: {}",NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
//        ExceptionEnum illegalArgumentError = ExceptionEnum.ILLEGAL_ARGUMENT_ERROR;
//        return ResponseEntity.status(illegalArgumentError.getHttpStatus()).body(ApiResponseResult.failure(illegalArgumentError));
//    }
//
//    //@Valid 유효성 검사에서 예외가 발생했을 때 -> requestbody에 잘못 들어왔을 때
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponseResult> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
//        log.error("[MethodArgumentNotValidException] cause: {}, message: {}",NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
//        ExceptionEnum invalidArgumentError = ExceptionEnum.INVALID_ARGUMENT_ERROR;
//        return ResponseEntity.status(invalidArgumentError.getHttpStatus()).body(ApiResponseResult.failure(invalidArgumentError));
//    }
//
//
//}
