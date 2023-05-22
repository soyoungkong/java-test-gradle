package com.mc.testexample.robot.aggregator;

import com.mc.testexample.robot.Robot;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

// 파라미터 인자를 여러개 사용할 경우
// 제약 조건 : public 클래스 이거나 static inner 클래스로 만들어야 함
public class RobotAggregator implements ArgumentsAggregator {

    @Override
    public Robot aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
        return new Robot(accessor.getInteger(0), accessor.getString(1));
    }
}
