package com.mc.testexample.robot;

import com.mc.testexample.robot.annotation.DevTest;
import com.mc.testexample.robot.annotation.LocalTest;
import com.mc.testexample.robot.annotation.ProdTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomTagTest {

    @Test
    @LocalTest
    void create_new_robot_all(){
        System.out.println("tag - local 테스트");
        assertTrue(1==1, "테스트 용도");
    }

    @Test
    @DevTest
    @DisplayName("속도가 빠른 테스트")
    void create_new_robot_fast(){
        System.out.println("tag - dev 테스트");
    }

    @Test
    @ProdTest
    @DisplayName("속도 느린 테스트")
    void create_new_robot_slow(){
        System.out.println("tag - prod 테스트");
    }
}