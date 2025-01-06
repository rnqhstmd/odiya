package org.example.odiya.auth.aop.argumentresolver;

import static org.junit.jupiter.api.Assertions.*;

import org.example.odiya.auth.aop.annotation.AuthMember;
import org.example.odiya.common.exception.UnauthorizedException;
import org.example.odiya.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.mockito.Mockito.*;

class AuthMemberArgumentResolverTest {

    private AuthMemberArgumentResolver resolver;

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private ModelAndViewContainer mavContainer;

    @Mock
    private NativeWebRequest webRequest;

    @Mock
    private WebDataBinderFactory binderFactory;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resolver = new AuthMemberArgumentResolver();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("@AuthMember 어노테이션이 있을 경우 true 를 반환한다")
    void supportsParameter_withAuthMemberAnnotation_returnsTrue() {
        when(methodParameter.hasParameterAnnotation(AuthMember.class)).thenReturn(true);

        assertTrue(resolver.supportsParameter(methodParameter));
    }

    @Test
    @DisplayName("@AuthMember 어노테이션이 없을 경우 false 를 반환한다")
    void supportsParameter_withoutAuthMemberAnnotation_returnsFalse() {
        when(methodParameter.hasParameterAnnotation(AuthMember.class)).thenReturn(false);

        assertFalse(resolver.supportsParameter(methodParameter));
    }

    @Test
    @DisplayName("인증된 회원이 있을 경우 해당 회원 객체를 반환한다")
    void resolveArgument_withAuthenticatedMember_returnsMember() throws Exception {
        Member member = Member.builder()
                .id(1L)
                .name("테스터")
                .email("abc@test.com")
                .password("abcd1234")
                .build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(member);

        Object result = resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        assertEquals(member, result);
    }

    @Test
    @DisplayName("인증 정보가 없을 경우 UnauthorizedException 을 발생시킨다")
    void resolveArgument_withoutAuthentication_throwsUnauthorizedException() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(UnauthorizedException.class, () ->
                resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory));
    }

    @Test
    @DisplayName("Principal 이 Member 타입이 아닐 경우 UnauthorizedException 을 발생시킨다")
    void resolveArgument_withNonMemberPrincipal_throwsUnauthorizedException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new Object());

        assertThrows(UnauthorizedException.class, () ->
                resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory));
    }
}