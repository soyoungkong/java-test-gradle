package com.mc.testexample.robot;

import org.junit.jupiter.api.*;

// Order는 낮을 수록 우선순위가 높다.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderTest {

    int value = 1;

    @Test
    @Order(3)
    void check_instance1(){
        System.out.println("1 : " + this.toString() +", value : " + (value++));
    }

    @Test
    @Order(1)
    void check_instance2(){
        System.out.println("2 : " + this.toString() +", value : " + (value++));
    }

    @Test
    @Order(2)
    void check_instance3() {
        System.out.println("3 : " + this.toString() + ", value : " + (value++));
    }

    @Test
    @Order(1)
    void check_instance4() {
        System.out.println("4 : " + this.toString() + ", value : " + (value++));
    }
}
