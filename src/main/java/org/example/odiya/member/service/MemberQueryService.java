package org.example.odiya.member.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.common.exception.NotFoundException;
import org.example.odiya.member.domain.Member;
import org.example.odiya.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.odiya.common.exception.type.ErrorType.MEMBER_NOT_FOUND_ERROR;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public Member findExistingMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND_ERROR));
    }

    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }
}
