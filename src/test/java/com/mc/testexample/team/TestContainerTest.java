package com.mc.testexample.team;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Testcontainers // testContainer 테스트 용도 (DB 연동)
class TestContainerTest {

    @Mock MemberService memberService;

    @Autowired TeamRepository teamRepository;

//    @Container
//    static MySQLContainer mySQLContainer  = new MySQLContainer("mysql:5.7.38");

    @Container
    static GenericContainer customContainer  = new GenericContainer("mysql")
            .withEnv("MYSQL", "javatest") // key, value
            .withExposedPorts(15231) // 컨테이너가 노출하는 포즈와 호스트가 어떤 것으로 받을 지는 알 수 없다. 랜덤 (도커는 -p 1302:8080 식으로 가능)
            .waitingFor(Wait.forListeningPort()); // 만약 다른 어플리케이션이 뜰 때까지 기다리는 등 응답을 기다리는 동안 가용한지 기다렸다가 테스트를 진행함.

    @BeforeEach
    void beforeEach(){
        System.out.println(customContainer.getMappedPort(15231)); // 맵핑된 오리지널 호스트 포트 확인 방법
        System.out.println(customContainer.getLogs());
        teamRepository.deleteAll();
    }

    @BeforeAll
    static void beforeALl(){
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
        customContainer.followOutput(logConsumer);
    }

    @Test
    void createTeamService(){
        // given
        TeamService teamService = new TeamService(memberService, teamRepository);

        Member member = new Member("kim");
        given(memberService.findById(1L)).willReturn(Optional.of(member));
        Team team = new Team("Software");
        System.out.println(team.toString());
        assertNull(team.getCreatedTime());

        // When
        team = teamService.createNewTeam(1L, team);

        // then
        System.out.println(team.toString());
        assertNotNull(team.getCreatedTime());
    }
}