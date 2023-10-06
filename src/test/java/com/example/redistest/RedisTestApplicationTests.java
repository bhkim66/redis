package com.example.redistest;

import com.example.redistest.repository.UserAccessRepository;
import com.example.redistest.vo.UserAccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
		String userId = "bhkim";
		String userName = "bh";
		String userHome = "seoul";
		Long remainMs = 3000L;
		UserAccessToken userAccessToken = UserAccessToken.createUserAccessToken(userId, userName, userHome, remainMs);

		userAccessRepository.save(userAccessToken);

		UserAccessToken userToken = userAccessRepository.findById(userId).get();

		assertAll(
				() -> assertEquals(userId, userToken.getId()),
				() -> assertEquals(userName, userToken.getUserName()),
				() -> assertEquals(userHome, userToken.getUserHome()),
				() -> assertEquals(remainMs/1000, userToken.getExpiration())
		);

		String userId2 = "bjkim";
		String userName2 = "bj";
		UserAccessToken userAccessToken2 = UserAccessToken.createUserAccessToken(userId2, userName2, userHome, remainMs);

		userAccessRepository.save(userAccessToken2);

	}


}
