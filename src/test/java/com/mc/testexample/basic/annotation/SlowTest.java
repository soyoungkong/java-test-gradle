package com.mc.testexample.basic.annotation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Test
@Target(ElementType.METHOD) // 메소드에 사용
@Retention(RetentionPolicy.RUNTIME) // 어노테이션 정보가 런타임까지 유지
public @interface SlowTest {
}
