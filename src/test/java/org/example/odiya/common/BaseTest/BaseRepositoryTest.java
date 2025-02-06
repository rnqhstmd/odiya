package org.example.odiya.common.BaseTest;

import jakarta.persistence.EntityManager;
import org.example.odiya.common.Fixture.FixtureGenerator;
import org.example.odiya.common.config.FixtureGeneratorConfig;
import org.example.odiya.common.config.JpaConfig;
import org.example.odiya.eta.repository.EtaRepository;
import org.example.odiya.mate.repository.MateRepository;
import org.example.odiya.meeting.repository.MeetingRepository;
import org.example.odiya.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import({JpaConfig.class, FixtureGeneratorConfig.class})
@DataJpaTest
@ActiveProfiles("test")
public abstract class BaseRepositoryTest {

    @Autowired
    protected FixtureGenerator fixtureGenerator;

    @Autowired
    protected MateRepository mateRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected MeetingRepository meetingRepository;

    @Autowired
    protected EtaRepository etaRepository;

    @Autowired
    protected EntityManager entityManager;
}
