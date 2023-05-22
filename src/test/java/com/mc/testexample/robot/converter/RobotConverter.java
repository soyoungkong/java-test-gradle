package com.mc.testexample.robot.converter;

import com.mc.testexample.robot.Robot;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

// SimpleArgumentConvert는 하나의 argument만 사용할 수 있음.
public class RobotConverter extends SimpleArgumentConverter {

    @Override
    protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
        assertEquals(Robot.class, targetType, "Can only convert to Robot");
        return new Robot(Integer.parseInt(source.toString()));
    }
}
