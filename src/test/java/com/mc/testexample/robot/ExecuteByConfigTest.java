package com.mc.testexample.robot;

import com.mc.testexample.common.Region;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableConfigurationProperties
@SpringBootTest(classes=Robot.class)
class ExecuteByConfigTest {

    @Value("${app.type}")
    private String type;

    @Test
    void execute_by_config(){
        assertEquals(Region.EU.name(), type);

        if(type.equals(Region.EU.name())){
            System.out.println(Region.EU.name());
        }else{
            System.out.println(Region.N_EU.name());
        }
    }
}