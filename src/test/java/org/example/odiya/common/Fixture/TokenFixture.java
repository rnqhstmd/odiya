package org.example.odiya.common.Fixture;

import org.example.odiya.member.service.MemberQueryService;
import org.example.odiya.security.jwt.provider.JwtProvider;
import org.springframework.test.util.ReflectionTestUtils;

public class TokenFixture {

    public static final String TEST_SECRET_KEY = "test_secret_key_for_testing_purposes_only";
    public static final int VALID_EXPIRE_LENGTH = 3600000;  // 1hour
    public static final int EXPIRED_LENGTH = 0;
    public static final int SHORT_EXPIRE_LENGTH = 1000;  // 1second

    private TokenFixture() {
    }

    public static JwtProvider createValidJwtProvider(MemberQueryService memberQueryService) {
        JwtProvider provider = new JwtProvider(memberQueryService);
        ReflectionTestUtils.setField(provider, "key", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(provider, "validityInMilliseconds", VALID_EXPIRE_LENGTH);
        return provider;
    }

    public static JwtProvider createExpiredJwtProvider(MemberQueryService memberQueryService) {
        JwtProvider provider = new JwtProvider(memberQueryService);
        ReflectionTestUtils.setField(provider, "key", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(provider, "validityInMilliseconds", EXPIRED_LENGTH);
        return provider;
    }

    public static JwtProvider createInvalidJwtProvider(MemberQueryService memberQueryService) {
        JwtProvider provider = new JwtProvider(memberQueryService);
        ReflectionTestUtils.setField(provider, "key", "invalid_secret_key");
        ReflectionTestUtils.setField(provider, "validityInMilliseconds", VALID_EXPIRE_LENGTH);
        return provider;
    }

    public static JwtProvider createShortExpireJwtProvider(MemberQueryService memberQueryService) {
        JwtProvider provider = new JwtProvider(memberQueryService);
        ReflectionTestUtils.setField(provider, "key", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(provider, "validityInMilliseconds", SHORT_EXPIRE_LENGTH);
        return provider;
    }
}
