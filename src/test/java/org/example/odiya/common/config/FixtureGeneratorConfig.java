package org.example.odiya.common.config;

import org.example.odiya.common.Fixture.FixtureGenerator;
import org.example.odiya.eta.repository.EtaRepository;
import org.example.odiya.mate.repository.MateRepository;
import org.example.odiya.meeting.repository.MeetingRepository;
import org.example.odiya.member.repository.MemberRepository;
import org.example.odiya.notification.repository.NotificationRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Profile("test")
@TestConfiguration
public class FixtureGeneratorConfig {

    @Bean
    public FixtureGenerator fixtureGenerator(
            MemberRepository memberRepository,
            MeetingRepository meetingRepository,
            MateRepository mateRepository,
            EtaRepository etaRepository,
            NotificationRepository notificationRepository
    ) {
        return new FixtureGenerator(
                memberRepository,
                meetingRepository,
                mateRepository,
                etaRepository,
                notificationRepository
        );
    }
}
