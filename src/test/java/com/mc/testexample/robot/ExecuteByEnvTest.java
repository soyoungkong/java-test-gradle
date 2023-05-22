package com.mc.testexample.robot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.*;

class ExecuteByEnvTest {

    /* 시스템 환경변수에 따른 실행 */

    // 코드로 실행
//    @Test
//    @DisplayName("시스템 환경변수 값에 따른 실행 테스트")
//    void execute_by_system_env(){
//        String test_env = System.getenv("ENV");
//        assumingThat(test_env.equalsIgnoreCase("LOCAL"), () -> {
//            System.out.println("local");
//        });
//
//        assumingThat(test_env.equalsIgnoreCase("DEV"), () -> {
//            System.out.println("dev");
//        });
//    }

    // 어노테이션으로 실행
    @Test
    @EnabledIfEnvironmentVariable(named = "ENV", matches = "LOCAL")
    @DisplayName("로컬 환경변수일 때만 실행")
    void excute_local_env(){
        System.out.println("local");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "ENV", matches = "DEV")
    @DisplayName("DEV 환경변수일 때만 실행")
    void excute_dev_env(){
        System.out.println("dev");
    }

    /* 특정 OS에 따른 경우 */
    @Test
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("윈도우일 때만 실행")
    void execute_if_win(){
        System.out.println("win");
    }

    @Test
    @EnabledOnOs(OS.MAC)
    @DisplayName("맥일 때만 실행")
    void execute_if_mac(){
        System.out.println("mac");
    }

    @Test
    @EnabledOnOs({OS.MAC, OS.LINUX, OS.SOLARIS})
    @DisplayName("윈도우가 아닌 몇몇 경우만 실행")
    void execute_if_not_win(){
        System.out.println("not win");
    }

    /* 특정 Java version */
    @Test
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_9, JRE.JAVA_10, JRE.JAVA_11, JRE.JAVA_17})
    @DisplayName("java 8 이상")
    void execute_jre_above_8(){
        System.out.println("JRE above 8");
    }

    @Test
    @EnabledOnJre({JRE.OTHER})
    @DisplayName("java 8 이상이 아닌 경우")
    void execute_jre_under_8(){
        System.out.println("JRE under 8");
    }

}