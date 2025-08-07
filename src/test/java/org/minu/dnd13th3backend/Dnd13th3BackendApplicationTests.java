package org.minu.dnd13th3backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class Dnd13th3BackendApplicationTests {

    @MockBean // <-- 이 라인을 추가
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void contextLoads() {
    }

}
