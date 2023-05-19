package com.mc.testexample.basic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class Robot {
    private String name = "unused";
    private RobotStatus status = RobotStatus.AWAITING;
    private int timeout;

    public Robot(int timeout){
        if(timeout < 0) throw new IllegalArgumentException("timeout 설정값은 0보다 커야합니다.");
        this.timeout = timeout;
    }

    public Robot(int timeout, String name){
        this.name = name;
        this.timeout = timeout;
    }
}
