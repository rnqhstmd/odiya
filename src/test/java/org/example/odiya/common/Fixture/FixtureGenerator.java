package org.example.odiya.common.Fixture;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.odiya.eta.domain.Eta;
import org.example.odiya.eta.repository.EtaRepository;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.repository.MateRepository;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.repository.MeetingRepository;
import org.example.odiya.member.domain.Member;
import org.example.odiya.member.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FixtureGenerator {

    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final MateRepository mateRepository;
    private final EtaRepository etaRepository;

    public FixtureGenerator(MemberRepository memberRepository, MeetingRepository meetingRepository, MateRepository mateRepository, EtaRepository etaRepository) {
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
        this.mateRepository = mateRepository;
        this.etaRepository = etaRepository;
    }

    // Meeting 생성
    public Meeting generateMeeting() {
        LocalDateTime now = LocalDateTime.now();
        String randomCode = generateInviteCode();
        return meetingRepository.save(Meeting.builder()
                .name("테스트 모임")
                .date(now.toLocalDate())
                .time(now.toLocalTime())
                .target(Fixture.TARGET_LOCATION)
                .inviteCode(randomCode)
                .build());
    }

    public Meeting generateMeeting(LocalDateTime meetingTime) {
        String randomCode = generateInviteCode();
        return meetingRepository.save(Meeting.builder()
                .name("테스트 모임")
                .date(meetingTime.toLocalDate())
                .time(meetingTime.toLocalTime())
                .target(Fixture.TARGET_LOCATION)
                .inviteCode(randomCode)
                .build());
    }

    private String generateInviteCode() {
        return RandomStringUtils.randomNumeric(6);
    }

    public Meeting generateOverdueMeeting() {
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        Meeting meeting = generateMeeting(past);
        meeting.setOverdue(true);
        return meetingRepository.save(meeting);
    }

    // Member 생성
    public Member generateMember() {
        return generateMember("테스트 유저");
    }

    public Member generateMember(String name) {
        return memberRepository.save(Member.builder()
                .name(name)
                .email(name + UUID.randomUUID() + "@test.com")
                .password("password")
                .build());
    }

    // Mate 생성
    public Mate generateMate() {
        Meeting meeting = generateMeeting();
        Member member = generateMember();
        return generateMate(meeting, member);
    }

    public Mate generateMate(Meeting meeting) {
        return generateMate(meeting, generateMember());
    }

    public Mate generateMate(Member member) {
        return generateMate(generateMeeting(), member);
    }

    public Mate generateMate(Meeting meeting, Member member) {
        return mateRepository.save(Mate.builder()
                .member(member)
                .meeting(meeting)
                .origin(Fixture.ORIGIN_LOCATION)
                .estimatedTime(30L)
                .build());
    }

    // Eta 생성
    public Eta generateEta() {
        return generateEta(generateMate());
    }

    public Eta generateEta(Mate mate) {
        return generateEta(mate, 30L);
    }

    public Eta generateEta(Mate mate, long remainingMinutes) {
        return etaRepository.save(new Eta(mate, remainingMinutes));
    }

    public Eta generateArrivedEta(Mate mate) {
        Eta eta = generateEta(mate, 0L);
        eta.markAsArrived();
        return etaRepository.save(eta);
    }

    public Eta generateMissingEta(Mate mate) {
        Eta eta = generateEta(mate, 0L);
        eta.markAsMissing();
        return etaRepository.save(eta);
    }

    // 복합 시나리오 생성
    public MeetingWithMatesData generateMeetingWithMates(int mateCount) {
        Meeting meeting = generateMeeting();
        List<Mate> mates = IntStream.range(0, mateCount)
                .mapToObj(i -> generateMate(meeting))
                .collect(Collectors.toList());
        return new MeetingWithMatesData(meeting, mates);
    }

    public EtaScenarioData generateEtaScenario() {
        Meeting meeting = generateMeeting();

        Mate arrivedMate = generateMate(meeting);
        Eta arrivedEta = generateArrivedEta(arrivedMate);

        Mate missingMate = generateMate(meeting);
        Eta missingEta = generateMissingEta(missingMate);

        Mate movingMate = generateMate(meeting);
        Eta movingEta = generateEta(movingMate, 30L);

        return new EtaScenarioData(meeting, arrivedMate, arrivedEta,
                missingMate, missingEta, movingMate, movingEta);
    }

    public static class MeetingWithMatesData {
        private final Meeting meeting;
        private final List<Mate> mates;

        public MeetingWithMatesData(Meeting meeting, List<Mate> mates) {
            this.meeting = meeting;
            this.mates = mates;
        }

        public Meeting getMeeting() {
            return meeting;
        }

        public List<Mate> getMates() {
            return mates;
        }
    }

    public static class EtaScenarioData {
        private final Meeting meeting;
        private final Mate arrivedMate;
        private final Eta arrivedEta;
        private final Mate missingMate;
        private final Eta missingEta;
        private final Mate movingMate;
        private final Eta movingEta;

        public EtaScenarioData(Meeting meeting, Mate arrivedMate, Eta arrivedEta, Mate missingMate, Eta missingEta, Mate movingMate, Eta movingEta) {
            this.meeting = meeting;
            this.arrivedMate = arrivedMate;
            this.arrivedEta = arrivedEta;
            this.missingMate = missingMate;
            this.missingEta = missingEta;
            this.movingMate = movingMate;
            this.movingEta = movingEta;
        }

        public Meeting getMeeting() {
            return meeting;
        }

        public Mate getArrivedMate() {
            return arrivedMate;
        }

        public Eta getArrivedEta() {
            return arrivedEta;
        }

        public Mate getMissingMate() {
            return missingMate;
        }

        public Eta getMissingEta() {
            return missingEta;
        }

        public Mate getMovingMate() {
            return movingMate;
        }

        public Eta getMovingEta() {
            return movingEta;
        }
    }
}
