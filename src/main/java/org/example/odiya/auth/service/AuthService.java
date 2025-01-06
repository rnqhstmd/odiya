package org.example.odiya.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.auth.dto.SigninRequest;
import org.example.odiya.common.exception.ConflictException;
import org.example.odiya.member.domain.Member;
import org.example.odiya.member.service.MemberQueryService;
import org.example.odiya.member.service.MemberService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.odiya.common.exception.type.ErrorType.DUPLICATION_EMAIL_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final MemberQueryService memberQueryService;
    private final PasswordEncoder passwordEncoder;

    public void signUp(SigninRequest request) {
        validateDuplicateEmail(request.email());

        Member newMember = Member.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        // 초대코드 생성
        newMember.generateInviteCode();

        // 초대코드 중복 검증
        while (memberQueryService.existsByInviteCode(newMember.getInviteCode())) {
            newMember.generateInviteCode();
        }

        memberService.saveMember(newMember);
    }

    // 이메일 중복 검증
    private void validateDuplicateEmail(String email) {
        if (memberQueryService.existsByEmail(email)) {
            throw new ConflictException(DUPLICATION_EMAIL_ERROR);
        }
    }
}
