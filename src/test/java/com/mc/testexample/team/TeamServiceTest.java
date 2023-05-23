package com.mc.testexample.team;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Testcontainers // testContainer 테스트 용도 (DB 연동)
class TeamServiceTest {

    @Mock MemberService memberService;

    @Autowired TeamRepository teamRepository;

    @Container
    static MySQLContainer mySQLContainer  = new MySQLContainer("mysql:5.7.38");

    @BeforeEach
    void beforeAll(){
        teamRepository.deleteAll();
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