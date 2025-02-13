package org.example.odiya.common.Fixture;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.odiya.eta.domain.Eta;
import org.example.odiya.eta.repository.EtaRepository;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.repository.MateRepository;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.repository.MeetingRepository;
import org.example.odiya.member.domain.DeviceToken;
import org.example.odiya.member.domain.Member;
import org.example.odiya.member.repository.MemberRepository;
import org.example.odiya.notification.domain.FcmTopic;
import org.example.odiya.notification.domain.Notification;
import org.example.odiya.notification.domain.NotificationStatus;
import org.example.odiya.notification.domain.NotificationType;
import org.example.odiya.notification.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class FixtureGenerator {

    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final MateRepository mateRepository;
    private final EtaRepository etaRepository;
    private final NotificationRepository notificationRepository;

    public FixtureGenerator(
            MemberRepository memberRepository,
            MeetingRepository meetingRepository,
            MateRepository mateRepository,
            EtaRepository etaRepository,
            NotificationRepository notificationRepository
    ) {
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
        this.mateRepository = mateRepository;
        this.etaRepository = etaRepository;
        this.notificationRepository = notificationRepository;
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
                .deviceToken(new DeviceToken(UUID.randomUUID() + "deviceToken"))
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

    // 기본 알림 생성
    public Notification generateNotification() {
        return generateNotification(generateMate());
    }

    // Mate와 함께 알림 생성
    public Notification generateNotification(Mate mate) {
        return generateNotification(mate, NotificationType.REMINDER, NotificationStatus.PENDING);
    }

    // 타입과 상태를 지정하여 알림 생성
    public Notification generateNotification(
            Mate mate,
            NotificationType type,
            NotificationStatus status
    ) {
        return notificationRepository.save(Notification.builder()
                .mate(mate)
                .type(type)
                .status(status)
                .sendAt(LocalDateTime.now())
                .fcmTopic(new FcmTopic(mate.getMeeting()))
                .build());
    }

    // 특정 시간의 알림 생성
    public Notification generateNotification(
            Mate mate,
            LocalDateTime sendAt,
            NotificationStatus status
    ) {
        return notificationRepository.save(Notification.builder()
                .mate(mate)
                .type(NotificationType.REMINDER)
                .status(status)
                .sendAt(sendAt)
                .fcmTopic(new FcmTopic(mate.getMeeting()))
                .build());
    }

    // DISMISSED 상태의 알림 생성
    public Notification generateDismissedNotification(Mate mate) {
        return generateNotification(mate, NotificationType.REMINDER, NotificationStatus.DISMISSED);
    }

    // DONE 상태의 알림 생성
    public Notification generateDoneNotification(Mate mate) {
        return generateNotification(mate, NotificationType.REMINDER, NotificationStatus.DONE);
    }

    // PENDING 상태의 알림 생성
    public Notification generatePendingNotification(Mate mate) {
        return generateNotification(mate, NotificationType.REMINDER, NotificationStatus.PENDING);
    }

    // 여러 개의 알림 생성
    public List<Notification> generateNotifications(Mate mate, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> generateNotification(mate))
                .toList();
    }

    // 특정 타입의 여러 알림 생성
    public List<Notification> generateNotifications(
            Mate mate,
            NotificationType type,
            NotificationStatus status,
            int count
    ) {
        return IntStream.range(0, count)
                .mapToObj(i -> generateNotification(mate, type, status))
                .toList();
    }
}
