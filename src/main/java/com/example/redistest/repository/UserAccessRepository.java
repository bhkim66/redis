package com.example.redistest.repository;

import com.example.redistest.vo.UserAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccessRepository extends JpaRepository<UserAccessToken, String> {
    Optional<UserAccessToken> findByUserName(String userName);
}
