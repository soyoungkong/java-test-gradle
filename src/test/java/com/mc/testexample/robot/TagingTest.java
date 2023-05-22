package com.mc.testexample.robot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TagingTest {

    @Test
    @Tag("local")
    @Tag("fast")
    @Tag("multi")
    @DisplayName("속도가 빠른 테스트")
    void create_new_robot_all(){
        System.out.println("tag - local 테스트");
        assertTrue(1==1, "테스트 용도");
    }

    @Test
    @Tag("dev")
    @Tag("fast")
    @Tag("multi")
    @DisplayName("속도가 빠른 테스트")
    void create_new_robot_fast(){
        System.out.println("tag - dev 테스트");
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Robot(10);
            Thread.sleep(10);
        });
    }

    @Test
    @Tag("prod")
    @Tag("slow")
    @Tag("single")
    @DisplayName("속도 느린 테스트")
    void create_new_robot_slow(){
        System.out.println("tag - prod 테스트");
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Robot(10);
            Thread.sleep(1000);
        });
    }
}