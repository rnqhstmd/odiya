package org.example.odiya.member.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.member.domain.Member;
import org.example.odiya.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void saveMember(Member member) {
        memberRepository.save(member);
    }
}
