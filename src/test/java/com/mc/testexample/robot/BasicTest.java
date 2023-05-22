package com.mc.testexample.robot;

import com.mc.testexample.robot.Basic;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Public 필요 없음 : Junit5부터는 class나 method가 public일 필요 없음 (Junit4는 public이었어야함)
 *                  - 자바 리플렉션 사용. 굳이 public 사용할 필요 없음
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BasicTest {

    @Test
    @DisplayName("생성 1")
    void create_1(){
        Basic basic = new Basic();
        assertNotNull(basic);
        System.out.println("create1");
    }

    @Test
    @DisplayName("생성 2")
    void create_2(){
        System.out.println("create2");
    }

    @Test
    @DisplayName("생성 3")
    @Disabled // 테스트를 실행하지 않을 때(해당 소스가 Deprecated 된 경우). 자주 사용하지 않는 것이 좋음. Junit4의 @Ignored
    void create_3(){
        System.out.println("create3");
    }

    /**
     * 테스트를 실행하기 전에 딱 한번만 실행
     * - static. 무조건 static void로 작성
     * - private X, default O
     * - return이 있으면 안됨.
     */
    @BeforeAll // Junit4의 @BeforeClass
    static void beforeAll(){
        System.out.println("before all");
    }

    /**
     * 테스트를 실행 후 딱 한번만 실행
     * 조건 사항은 @BeforeAll과 같음
     */
    @AfterAll // Junit4의 @AfterClass
    static void afterAll(){
        System.out.println("after all");
    }

    /**
     * 모든 테스트 실행 전, 각각의 클래스와 메소드를 실행할 때 한번씩 실행.
     * static일 필요 없음.
     */
    @BeforeEach // Junit4의 @Before
    void beforeEach(){
        System.out.println("before each");
    }

    /**
     * 모든 테스트 실행 후, 각각의 클래스와 메소드를 실행할 때 한번씩 실행.
     * static일 필요 없음.
     */
    @AfterEach // Junit4의 @After
    void afterEach(){
        System.out.println("after each");
    }
}