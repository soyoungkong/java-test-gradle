package com.mc.testexample.robot;

import com.mc.testexample.robot.extension.FindSlowTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;


//@ExtendWith(FindSlowTestExtension.class) // 1. 선언적인 방법
public class ExtensionTest {

    @RegisterExtension  // 2. 프로그래밍적인 방법
    static FindSlowTestExtension findSlowTestExtension = new FindSlowTestExtension(500L);

    @Test
    void slowTest() throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("Slow Method for extension test");
    }
}
