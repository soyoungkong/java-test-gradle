package com.mc.testexample.team;

import java.util.Optional;

public class TeamService {
    private final MemberService memberService;
    private final TeamRepository repository;

    public TeamService(MemberService memberService, TeamRepository repository) {
        // assert를 사용해서 null 체크
        assert memberService != null;
        assert repository != null;
        this.memberService = memberService;
        this.repository = repository;
    }

    public Team createNewTeam(Long memberId, Team team){
        Optional<Member> member = memberService.findById(memberId);
        team.setMember(member.orElseThrow(()-> new IllegalArgumentException("Parts doesn't exist for id :" + memberId)));

        team = repository.save(team);
        memberService.notify(team); // verify 테스트용
        memberService.notify(member.get()); // verify 테스트용
        memberService.findById(memberId); // verify 테스트용
        return team;
    }
}
