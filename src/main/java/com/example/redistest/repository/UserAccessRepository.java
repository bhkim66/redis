package com.example.redistest.repository;

import com.example.redistest.entity.MemberMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserAccessRepository extends JpaRepository<MemberMaster, String> {
    Optional<MemberMaster> findByMemId(String MemId);
}
