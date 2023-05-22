package com.mc.testexample.robot;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

// JUnit은 기본 전략으로 테스트 메소드마다 테스트 인스턴스를 새로 만든다.
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)를 사용하면 클래스마다 인스턴스를 만들도록 전략을 변경할 수 있다.
// 이 때는 @BeforeAll, @AfterAll을 static으로 만들지 않아도 된다.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InstanceTest {

    int value = 1;

    @Test
    void check_instance1(){
        System.out.println("1 : " + this.toString() +", value : " + (value++));
    }

    @Test
    void check_instance2(){
        System.out.println("2 : " + this.toString() +", value : " + (value++));
    }

    @BeforeAll
    void beforeAll(){
        System.out.println("시작 시 한 번 실행");
    }

    @AfterAll
    void afterAll(){
        System.out.println("종료 시 한 번 실행");
    }
}
