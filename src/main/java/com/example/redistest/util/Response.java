package com.example.redistest.util;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class Response {

    @Getter
    @Builder
    private static class Body {
        private int state;
        private String result;
        private String massage;
        private Object data;
        private Object error;
    }

    public ResponseEntity<?> success(Object data, String msg, HttpStatus status) {
        return ResponseEntity.ok(Body.builder()
                .state(status.value())
                .data(data)
                .result("success")
                .massage(msg)
                .error(Collections.emptyList())
                .build());
    }

    public ResponseEntity<?> success(String msg, HttpStatus status) {
        return ResponseEntity.ok(Body.builder()
                .state(status.value())
                .result("success")
                .massage(msg)
                .error(Collections.emptyList())
                .build());
    }
    public ResponseEntity<?> failed(String msg, HttpStatus status) {
        return ResponseEntity.ok(Body.builder()
                .state(status.value())
                .result("failed")
                .massage(msg)
                .error(Collections.emptyList())
                .build());
    }
}
