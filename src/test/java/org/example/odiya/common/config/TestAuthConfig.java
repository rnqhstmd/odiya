package org.example.odiya.common.config;

import org.example.odiya.common.Fixture.TokenFixture;
import org.example.odiya.member.service.MemberQueryService;
import org.example.odiya.common.security.jwt.provider.JwtProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.util.ReflectionTestUtils;

@Profile("test")
@TestConfiguration
public class TestAuthConfig {

    @Bean
    public JwtProvider jwtProvider(MemberQueryService memberQueryService) {
        JwtProvider provider = new JwtProvider(memberQueryService);
        ReflectionTestUtils.setField(provider, "key", TokenFixture.TEST_SECRET_KEY);
        ReflectionTestUtils.setField(provider, "validityInMilliseconds", TokenFixture.VALID_EXPIRE_LENGTH);
        return provider;
    }
}
