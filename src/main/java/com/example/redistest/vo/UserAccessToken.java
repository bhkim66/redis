//package com.example.redistest.vo;
//
//import lombok.Builder;
//import lombok.Getter;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.redis.core.RedisHash;
//import org.springframework.data.redis.core.TimeToLive;
//import org.springframework.data.redis.core.index.Indexed;
//
//@Getter
//@RedisHash(value = "User", timeToLive = 60L)
//@Builder
//
//public class MemberAccessToken {
//
//    @Id
//    private String id;
//
//    @Indexed
//    private String memberName;
//
//    private String memberHome;
//
//    private String memberIp;
//
//    @TimeToLive
//    private Long expiration;
//
//    public static MemberAccessToken createUserAccessToken(String userName, String userHome, String userIp, Long remainMs) {
//        return MemberAccessToken.builder()
//                .memberName(userName)
//                .memberHome(userHome)
//                .memberIp(userIp)
//                .expiration(remainMs/1000)
//                .build();
//    }
//}
