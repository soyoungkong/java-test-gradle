package com.mc.testexample.robot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Robot {
    private String name = "unused";
    private Status status = Status.AWAITING;
    private int timeout;
    private Part part;
    private ModelStatus modelStatus = ModelStatus.PREPARING;
    private LocalDateTime openedDateTime;



    public Robot(int timeout){
        if(timeout < 0) throw new IllegalArgumentException("timeout 설정값은 0보다 커야합니다.");
        this.timeout = timeout;
    }

    public Robot(int timeout, String name){
        this.name = name;
        this.timeout = timeout;
    }

    // 신규 출시
    public void release() {
       this.modelStatus = ModelStatus.RELEASED;
       this.openedDateTime = LocalDateTime.now();
    }
}
