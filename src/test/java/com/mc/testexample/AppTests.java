package com.mc.testexample;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class AppTests {

    /**
     * Junit Platform으로 인해서 클래스 내의 테스트 코드(@Test 메소드) 내에서 실행
     * import static org.junit.jupiter.api.Assertions.* -> Junit5는 구현체인 Jupiter를 사용 (3,4는 Vintage사용)
     */
    @Test
    void contextLoads() {
        assertNotNull(App.class);
    }
}
