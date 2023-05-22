package com.mc.testexample.robot;

import com.mc.testexample.robot.PartService;
import com.mc.testexample.robot.RobotRepository;
import com.mc.testexample.robot.RobotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RobotServiceMockTest {

//    // 1-1) mock 생성 : mock 메소드를 이용해서 구현
//    @Test
//    void createRobotService(){
//
//        RobotRepository robotRepository = mock(RobotRepository.class);
//        PartService partService = mock(PartService.class);
//
//        RobotService robotService = new RobotService(partService, robotRepository);
//        assertNotNull(robotService, "Robot Service cannot be null");
//    }

//    // 1-2) mock 생성 : @Mock 어노테이션으로
//    @Mock
//    PartService partService;
//    @Mock
//    RobotRepository robotRepository;
//
//    @Test
//    void createRobotService(){
//        RobotService robotService = new RobotService(partService, robotRepository);
//        assertNotNull(robotService, "Robot Service cannot be null");
//    }

//    // 1-3) mock 생성 : 파라미터에 어노테이션으로 - 전역 @Mock 필요없음
//    @Test
//    void createRobotService(@Mock PartService partService, @Mock RobotRepository robotRepository){
//        RobotService robotService = new RobotService(partService, robotRepository);
//        assertNotNull(robotService, "Robot Service cannot be null");
//    }

    // 2-1. Mock Stubbing
    @Test
    void createRobotService(@Mock PartService partService, @Mock RobotRepository robotRepository){
        // partService.validate(1L); // void를 반환하는 메소드는 아무 일도 일어나지 않음. 예외x

        RobotService robotService = new RobotService(partService, robotRepository);

        Part part = new Part(1L, "wheel");

        // Stubbing : 원하는 값이 나오도록 함. stubbing이 안 되어있으면 진행x
        when(partService.findById(1L)).thenReturn(Optional.of(part));
//      when(partService.findById(2L)).thenThrow(new RuntimeException()); // 예외 발생
        doThrow(new IllegalArgumentException()).when(partService).validate(1L); // void 값이 호출 되었을 떄, 예외 발생

        // 실패 케이스
//        Optional<Part> savedPart = partService.findById(1L);
//        assertEquals("camera", savedPart.get().getCode());

        assertThrows(IllegalArgumentException.class, ()-> {
            partService.validate(1L);
        });

        Robot robot = new Robot(1000, "robot1");
        robotService.createNewRobot(1L, robot);
    }

    // 2-2 Mock Stubbing. 동일 매개변수로 여러번 호출 시 다르게 결과값 내기
    @Test
    void createRobotService2(@Mock PartService partService){
        Part part = new Part(1L, "wheel");

        // 1번 정상, 2번 예외, 3번 빈값이 출력되도록 함.
        when(partService.findById(any()))
                .thenReturn(Optional.of(part))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.empty());

        assertEquals("wheel", partService.findById(1L).get().getCode());
        assertThrows(RuntimeException.class, () -> partService.findById(2L));
        assertEquals(Optional.empty(), partService.findById(3L));
    }

    // 2-3 Mock Stubbing 연습
    @Test
    void createRobotService3(@Mock PartService partService, @Mock RobotRepository robotRepository){
        RobotService robotService = new RobotService(partService, robotRepository);

        Robot robot  = new Robot (1500, "test robot");
        Part part = new Part(1L, "LiDAR");

        // 해당 stubbing이 되어있어야지만 테스트가 성공함
        when(partService.findById(1L)).thenReturn(Optional.of(part));
        when(robotRepository.save(robot)).thenReturn(robot);

        // 로봇에 파츠 셋팅
        robotService.createNewRobot(1L, robot);

        assertEquals(part, robot.getPart());
    }

    // 2-3 Mock Stubbing 연습
    @Test
    void verifyMock(@Mock PartService partService, @Mock RobotRepository robotRepository){
        RobotService robotService = new RobotService(partService, robotRepository);

        Robot robot  = new Robot (1500, "test robot");
        Part part = new Part(1L, "LiDAR");

        // 해당 stubbing이 되어있어야지만 테스트가 성공함
        when(partService.findById(1L)).thenReturn(Optional.of(part));
        when(robotRepository.save(robot)).thenReturn(robot);

        // 로봇에 파츠 셋팅
        robotService.createNewRobot(1L, robot);

        assertEquals(part, robot.getPart());

        // 3. 객체 검증 : createNewRobot에서 partService가 쓰인 횟수, 순서 등등
        verify(partService, times(1)).notify(robot); // 1번 이상 호출했는지
        // verifyNoMoreInteractions(partService); // 더는 partService가 호출되면 안됨 - 그러나 호출 된 것이 있어서 테스트 실패
        verify(partService, times(1)).notify(part); // 한번도 불리지 않은 것
        // verify(partService, times(1)).validate(1L); // 한번도 불리지 않은 것
        verify(partService, never()).validate(any()); // 전혀 호출되지 않아야함

        // 객체 검증 순서 확인 - 불린 순서대로 verify (순서 바꾸면 에러)
        InOrder inOrder = inOrder(partService);
        inOrder.verify(partService).notify(robot);
        inOrder.verify(partService).notify(part);

        // 특정 시점 이내에 호출 되었는지
        verify(partService, timeout(1).times(1)).notify(robot);
    }
}
