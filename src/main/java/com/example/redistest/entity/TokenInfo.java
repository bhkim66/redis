package com.example.redistest.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@ToString
public class TokenInfo {
    private String userId;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long rtkExpirationTime;
    private String rtkExpirationDate;
    private String userIp;
}
