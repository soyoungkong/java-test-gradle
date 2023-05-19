package com.mc.testexample.basic;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

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
