package com.mc.testexample.basic;

import com.mc.testexample.basic.aggregator.RobotAggregator;
import com.mc.testexample.basic.annotation.DevTest;
import com.mc.testexample.basic.annotation.LocalTest;
import com.mc.testexample.basic.annotation.ProdTest;
import com.mc.testexample.basic.converter.RobotConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RepeatTest {

    // @RepeatedTest : 횟수 정도만 지정
//    @RepeatedTest(10) // (횟수)
    @RepeatedTest(value=10, name = "{displayName}, {currentRepetition} / {totalRepetitions}")
    @DisplayName("반복 테스트 - Repeated")
    void repeatAfterMe(RepetitionInfo info){
        System.out.println("test : " + info.getCurrentRepetition() + "/" + info.getTotalRepetitions());
    }

    // @ParameterizedTest : 각기 다른 파라미터를 넣어서 반복 테스트, 암묵적인 타입 변환 실행됨
    @ParameterizedTest(name = "{index} : {displayName}  message= {0}")
    @NullAndEmptySource
    @ValueSource(strings = {"우리", "회사", "화이팅"} )
//    @EmptySource
//    @NullSource // 위에서 @NullAndEmptySource를 쓰면 이후에는 @EmptySource와 @NullSource는 먹히지 않음.
    @DisplayName("반복 Parameterized @ValueSource")
    void repeatByStrings(String message){
        System.out.println(message);
        assertTrue(message.length()>1, () -> "길이가 두 글자를 초과해야합니다.");
    }

    // SimpleArgumentConvert로 단일 인자값을 받아서 convert (명시적 타입 지정)
    @ParameterizedTest(name = "{index} : {displayName}  message= {0}")
    @ValueSource(ints = {11, 12, 13})
    @DisplayName("반복 Parameterized - SimpleArgumentConvert")
    void repeatByMultiValues(@ConvertWith(RobotConverter.class) Robot robot){
        assertTrue(robot.getTimeout()>11, () -> "Timeout은 10보다 커야합니다.");
    }

    // Argument aggregator - 직접 작성
    @ParameterizedTest(name = "{index} : {displayName}  message= {0}, {1}")
    @CsvSource({"11, '우리'", "12, '모두'","13, '직원'"} )
    @DisplayName("반복 Parameterized - ArgumentsAggregator 사용")
    void repeatUsingArgumentsAccessor(ArgumentsAccessor accessor){
        Robot robot = new Robot(accessor.getInteger(0), accessor.getString(1));
        System.out.println(robot.toString());
    }

    // Argument aggregator - custom 
    @ParameterizedTest(name = "{index} : {displayName}  message= {0}, {1}")
    @CsvSource({"11, '우리'", "12, '모두'","13, '직원'"} )
    @DisplayName("반복 Parameterized - custom ArgumentsAggregator 사용")
    void repeatUsingConverter(@AggregateWith(RobotAggregator.class) Robot robot){
        System.out.println(robot.toString());
    }
}