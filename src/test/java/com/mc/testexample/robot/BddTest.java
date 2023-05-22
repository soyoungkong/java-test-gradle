package com.mc.testexample.robot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BddTest {

    // RobotServiceMockTest의 verifyMock과 비교해보면서
    @Test
    void createRobotTest(@Mock PartService partService, @Mock RobotRepository robotRepository){
        // Given
        RobotService robotService = new RobotService(partService, robotRepository);
        Part part = new Part(1L, "wheel");
        Robot robot = new Robot(1000, "robot1");

        // Mockito의 when은 사실상 주어진 정보(Given)이라 표현이 좀 맞지 않는다.
        // 따라서 when을 사용하는 대신 Given을 쓰는 것이 보기 좋다.
        given(partService.findById(1L)).willReturn(Optional.of(part));
        given(robotRepository.save(robot)).willReturn(robot);
//        when(partService.findById(1L)).thenReturn(Optional.of(part));
//        when(robotRepository.save(robot)).thenReturn(robot);

        /* When */
        // 로봇에 파츠 셋팅
        robotService.createNewRobot(1L, robot);
        
        // Verify -> Then
        then(partService).should(times(1)).notify(robot);
        // then(partService).shouldHaveNoMoreInteractions(); // 더이상 인터렉션이 없는지 = verifyNoMoreInteractions(partService);
    }
}
