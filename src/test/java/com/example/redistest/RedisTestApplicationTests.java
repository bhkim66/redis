package com.example.redistest;

import com.example.redistest.repository.UserAccessRepository;
import com.example.redistest.vo.UserAccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RedisTestApplicationTests {
	@Autowired
	UserAccessRepository userAccessRepository;

	@BeforeEach
	void clear() {
		userAccessRepository.deleteAll();
	}

	@DisplayName("save")
	@Test
	void save() {
		String userName = "bhkim";
		String userHome = "seoul";
		Long remainMs = 1000L;
		UserAccessToken userAccessToken = UserAccessToken.createUserAccessToken(userName, userHome, remainMs);

		userAccessRepository.save(userAccessToken);

		Optional<UserAccessToken> userToken = userAccessRepository.findByUserName(userName);

		assertAll(
				() -> assertEquals(userName, userToken.getUserName()),
				() -> assertEquals(userHome, userToken.getUserHome()),
				() -> assertEquals(remainMs/1000, userToken.getExpiration())
		);
		System.out.println("user id : " + userToken.getId());
		System.out.println("user name : " + userToken.getUserName());

		String userName2 = "bjkim";
		UserAccessToken userAccessToken2 = UserAccessToken.createUserAccessToken(userName2, userHome, remainMs);

		userAccessRepository.save(userAccessToken2);

		UserAccessToken userToken2 = userAccessRepository.findByUserName(userName2).get();

		System.out.println("user id : " + userToken2.getId());
		System.out.println("user name : " + userToken2.getUserName());

	}


}
