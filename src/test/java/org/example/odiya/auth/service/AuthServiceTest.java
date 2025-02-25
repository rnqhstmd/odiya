package org.example.odiya.auth.service;

import org.example.odiya.auth.dto.request.LoginRequest;
import org.example.odiya.auth.dto.request.SigninRequest;
import org.example.odiya.auth.dto.response.LoginResponse;
import org.example.odiya.common.BaseTest.BaseServiceTest;
import org.example.odiya.common.Fixture.Fixture;
import org.example.odiya.common.exception.ConflictException;
import org.example.odiya.common.exception.UnauthorizedException;
import org.example.odiya.member.domain.Member;
import org.example.odiya.member.service.MemberQueryService;
import org.example.odiya.member.service.MemberService;
import org.example.odiya.common.security.jwt.provider.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest extends BaseServiceTest {

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberQueryService memberQueryService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공")
    void signUp_withValidRequest_savesMember() {
        // Given
        SigninRequest request = dtoGenerator.generateSigninRequest(Fixture.MEMBER1);
        when(memberQueryService.existsByEmail(any())).thenReturn(false);

        // When
        authService.signUp(request);

        // Then
        verify(memberService).saveMember(any(Member.class));
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입 실패한다.")
    void signUp_withDuplicateEmail_throwsConflictException() {
        // Given
        SigninRequest request = dtoGenerator.generateSigninRequest(Fixture.MEMBER1);
        when(memberQueryService.existsByEmail(request.email())).thenReturn(true);

        // When & Then
        assertThrows(ConflictException.class, () -> authService.signUp(request));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_withValidCredentials_returnsLoginResponse() {
        // Given
        Member member = fixtureGenerator.generateMember();
        String rawPassword = "password";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        member.updatePassword(encodedPassword);
        LoginRequest request = new LoginRequest(member.getEmail(), rawPassword);

        when(memberQueryService.findExistingMemberByEmail(any())).thenReturn(member);

        // When
        LoginResponse response = authService.login(request);

        // Then
        assertThat(response.accessToken()).isNotNull();
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 실패한다")
    void login_withInvalidPassword_throwsUnauthorizedException() {
        // Given
        Member member = fixtureGenerator.generateMember();
        String encodedPassword = passwordEncoder.encode("correctPassword");  // 실제로 암호화
        member.updatePassword(encodedPassword);
        LoginRequest request = new LoginRequest(member.getEmail(), "wrongPassword");

        when(memberQueryService.findExistingMemberByEmail(any())).thenReturn(member);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }
}