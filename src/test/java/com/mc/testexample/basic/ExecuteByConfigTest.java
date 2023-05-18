package com.mc.testexample.basic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableConfigurationProperties()
@SpringBootTest(classes=Robot.class)
class ExecuteByConfigTest {

    @Value("${server.address.ip}")
    private String property;

    @Test
    void execute_by_config(){
        System.out.println(property);
        assertEquals( "192.168.0.1", property);
    }
}