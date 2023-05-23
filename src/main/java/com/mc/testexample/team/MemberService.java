package com.mc.testexample.team;


import java.util.Optional;

public interface MemberService {
    Optional<Member> findById(Long partId);
    void validate(Long partId);

    void notify(Team team);

    void notify(Member member);
}
