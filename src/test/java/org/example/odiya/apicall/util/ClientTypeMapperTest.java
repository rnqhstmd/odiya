package org.example.odiya.apicall.util;

import org.example.odiya.apicall.domain.ClientType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientTypeMapperTest {

    @Test
    @DisplayName("대소문자 구분 없이 ClientType으로 변환한다")
    void from_IgnoreCase() {
        // given
        String upperCase = "GOOGLE";
        String mixedCase = "KaKaO";
        String lowerCase = "tmap";

        // when
        ClientType googleResult = ClientTypeMapper.from(upperCase);
        ClientType kakaoResult = ClientTypeMapper.from(mixedCase);
        ClientType tmapResult = ClientTypeMapper.from(lowerCase);

        // then
        assertThat(googleResult).isEqualTo(ClientType.GOOGLE);
        assertThat(kakaoResult).isEqualTo(ClientType.KAKAO);
        assertThat(tmapResult).isEqualTo(ClientType.TMAP);
    }
}