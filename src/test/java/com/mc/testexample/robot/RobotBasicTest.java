package com.mc.testexample.robot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class RobotBasicTest {
    @Test
    @DisplayName("로봇 클래스 생성")
    void create_new_robot() {
        Robot robot = new Robot(3);
        assertAll(
//                () -> {
//                    IllegalArgumentException exception =
//                            assertThrows(IllegalArgumentException.class, () -> new Robot(-10));
//                    assertEquals("timeout 설정값은 0보다 커야합니다.", exception.getMessage());
//                },
                () -> assertDoesNotThrow(() -> new Robot(-10)),
                () -> assertNotNull(robot),
                () -> assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
                    new Robot(10);
                    Thread.sleep(1000);
                }),
                () -> assertEquals(Status.AWAITING, robot.getStatus(), () -> "로봇 초기 상태는 무조건 " + Status.AWAITING + " 이어야 합니다."),
                () -> assertTrue(robot.getTimeout() > 10, () -> "로봇 타임 아웃값은 10보다 커야합니다.")
        );
    }

    @Test
    @DisplayName("로봇 초기상태 확인")
    void check_robot_initial_status() {
        Robot robot = new Robot();

        // String : 테스트 성공여부와 상관없이 계속 String을 만듦.
        //assertEquals(RobotStatus.AWAITING, robot.getStatus(), "로봇 초기 상태는 무조건 " + RobotStatus.AWAITING +"이어야 합니다.");

        // 람다식 : 필요할 때만 String을 만듦. (성능)
        //assertEquals(RobotStatus.AWAITING, robot.getStatus(), () -> "로봇 초기 상태는 무조건 " + RobotStatus.AWAITING +" 이어야 합니다.");

        // 에러 메시지를 만드는 방식이 복잡하면 Supplier<String>으로 작성, 간추리면 위와 같다.
        assertEquals(Status.AWAITING, robot.getStatus(), new Supplier<String>() {
            @Override
            public String get() {
                return "로봇 초기 상태는 무조건 " + Status.AWAITING + " 이어야 합니다.";
            }
        });
    }

    @Test
    @DisplayName("로봇 초기 타임아웃 값 확인")
    void check_initial_time_out() {
        Robot robot = new Robot(3);
        assertTrue(robot.getTimeout() > 10, () -> "로봇 타임 아웃값은 10보다 커야합니다.");
    }

    @Test
    @DisplayName("AssertJ 예제")
    void create_new_robot_with_assertJ() {
        Robot robot = new Robot(11);
        assertThat(robot.getTimeout()).isGreaterThan(12);
    }
}