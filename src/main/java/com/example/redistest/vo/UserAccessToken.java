package com.example.redistest.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash("User")
@Builder
public class UserAccessToken {

    @Id
    private String id;

    private String userName;

    private String userHome;

    @TimeToLive
    private Long expiration;

    public static UserAccessToken createUserAccessToken(String userName, String userHome, Long remainMs) {
        return UserAccessToken.builder()
                .userName(userName)
                .userHome(userHome)
                .expiration(remainMs/1000)
                .build();
    }
}
