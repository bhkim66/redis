package com.example.redistest.service;

import com.example.redistest.entity.MemberMaster;
import com.example.redistest.entity.User;
import com.example.redistest.repository.UserAccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {
    @Autowired
    private UserAccessRepository userAccessRepository;

    @Override
    public UserDetails loadUserByUsername(String memId) throws UsernameNotFoundException {
        log.info("loadUserByUsername");
        return userAccessRepository.findByMemId(memId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
    }

    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(MemberMaster member) {
        log.info("member {} ", member.getPassword());
        User user = new User(member.getMemId(), member.getPassword());

        log.info("user {} ", user);
        return new User(member.getMemId(), member.getPassword());
    }

}
