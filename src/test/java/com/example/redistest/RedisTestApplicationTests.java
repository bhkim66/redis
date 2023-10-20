package com.example.redistest;

import com.example.redistest.repository.UserAccessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@SpringBootTest
class RedisTestApplicationTests {
	@Autowired
	UserAccessRepository userAccessRepository;

	@Autowired
	RedisTemplate redisTemplate;

	@BeforeEach
	void clear() {
		userAccessRepository.deleteAll();
	}

	@DisplayName("save")
	@Test
	void save() {
		redisTemplate.opsForValue().set("key:test", "hello");
		redisTemplate.expire("key:test" , 10*1000L, MILLISECONDS);
		Long expiredTime1 = 10 * 60 * 1000L;
		Long expiredTime2 = 30 * 60 * 1000L;

		Map<String, Object> map = new HashMap<>();
		map.put("firstName", "BH");
		map.put("lastName", "KIM");
		map.put("gender", "Man");

		redisTemplate.opsForHash().putAll("User" , map);
		Map<String, Object> map2 = new HashMap<>();
		map2.put("firstName", "JH");
		map2.put("lastName", "PARK");
		map2.put("gender", "Women");

		redisTemplate.opsForHash().putAll("User2" , map2);
		redisTemplate.expire("User" , expiredTime1, MILLISECONDS);
		redisTemplate.expire("User2" , expiredTime2, MILLISECONDS);

		String firstName = (String) redisTemplate.opsForHash().get("User", "firstName");
		String lastName = (String) redisTemplate.opsForHash().get("User", "lastName");
		String gender = (String) redisTemplate.opsForHash().get("User", "gender");
		System.out.println(firstName);
		System.out.println(lastName);
		System.out.println(gender);

		firstName = (String) redisTemplate.opsForHash().get("User2", "firstName");
		lastName = (String) redisTemplate.opsForHash().get("User2", "lastName");
		gender = (String) redisTemplate.opsForHash().get("User2", "gender");
		System.out.println(firstName);
		System.out.println(lastName);
		System.out.println(gender);
		System.out.println(map2.get("firstName"));
//

	}


}
