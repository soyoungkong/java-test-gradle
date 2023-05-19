package com.mc.testexample.basic.custom;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Test
@Target(ElementType.METHOD) // 메소드에 사용
@Retention(RetentionPolicy.RUNTIME) // 어노테이션 정보가 런타임까지 유지 
@Tag("dev") // "dev"는 문자열이므로 타입이 안전하지 않음. (오타 발생 많음)
public @interface DevTest {

}
