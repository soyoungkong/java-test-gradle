package com.mc.testexample.basic;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Robot {
    private RobotStatus status = RobotStatus.AWAITING;
    private int timeout;

    public Robot(int timeout){
        if(timeout < 0) throw new IllegalArgumentException("timeout 설정값은 0보다 커야합니다.");
        this.timeout = timeout;
    }
}
