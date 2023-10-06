package com.example.redistest.repository;

import com.example.redistest.vo.UserAccessToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserAccessRepository extends CrudRepository<UserAccessToken, String> {
    Optional<UserAccessToken> findByUserName(String userName);
}
