package com.mc.testexample.robot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Part {
    Long id;
    String code;

    public Part(Long id, String code){
        this.id = id;
        this.code = code;
    }
}
