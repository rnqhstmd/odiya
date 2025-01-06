package org.example.odiya.auth.service;

import org.example.odiya.auth.dto.request.LoginRequest;
import org.example.odiya.auth.dto.request.SigninRequest;
import org.example.odiya.auth.dto.response.LoginResponse;
import org.example.odiya.common.exception.ConflictException;
import org.example.odiya.common.exception.UnauthorizedException;
import org.example.odiya.member.domain.Member;
import org.example.odiya.member.service.MemberQueryService;
import org.example.odiya.member.service.MemberService;
import org.example.odiya.security.jwt.provider.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Mock
    private MemberService memberService;

    @Mock
    private MemberQueryService memberQueryService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(memberService, memberQueryService, jwtProvider, passwordEncoder);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_withValidRequest_savesMember() {
        SigninRequest request = new SigninRequest("name", "email@example.com", "password");
        when(memberQueryService.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(memberQueryService.existsByInviteCode(anyString())).thenReturn(false);

        authService.signUp(request);

        verify(memberService).saveMember(any(Member.class));
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입 실패한다.")
    void signUp_withDuplicateEmail_throwsConflictException() {
        SigninRequest request = new SigninRequest("name", "email@example.com", "password");
        when(memberQueryService.existsByEmail(request.email())).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.signUp(request));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_withValidCredentials_returnsLoginResponse() {
        LoginRequest request = new LoginRequest("email@example.com", "password");
        Member member = Member.builder()
                .id(1L)
                .name("테스터")
                .email("abc@test.com")
                .password("abcd1234")
                .build();
        when(memberQueryService.findExistingMemberByEmail(request.email())).thenReturn(member);
        when(passwordEncoder.matches(request.password(), member.getPassword())).thenReturn(true);
        when(jwtProvider.generateJwtToken(member.getEmail())).thenReturn("jwtToken");

        LoginResponse response = authService.login(request);

        assertEquals("jwtToken", response.accessToken());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 실패한다")
    void login_withInvalidPassword_throwsUnauthorizedException() {
        LoginRequest request = new LoginRequest("fail@fail.com", "fail");
        Member member = Member.builder()
                .id(1L)
                .name("테스터")
                .email("abc@test.com")
                .password("abcd1234")
                .build();
        when(memberQueryService.findExistingMemberByEmail(request.email())).thenReturn(member);
        when(passwordEncoder.matches(request.password(), member.getPassword())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }
}