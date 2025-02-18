package org.example.odiya.notification.repository;

import org.example.odiya.common.BaseTest.BaseRepositoryTest;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.member.domain.Member;
import org.example.odiya.notification.domain.Notification;
import org.example.odiya.notification.domain.NotificationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationRepositoryTest extends BaseRepositoryTest {

    @Test
    @DisplayName("특정 Mate의 미래 알림들의 상태를 DISMISSED로 변경한다")
    void updateAllStatusToDismissedByMateIdAndSendAtAfterNow_Success() {
        // Given
        Member member = fixtureGenerator.generateMember();
        Meeting meeting = fixtureGenerator.generateMeeting();
        Mate mate = fixtureGenerator.generateMate(meeting, member);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureTime = now.plusHours(1);

        fixtureGenerator.generateNotification(mate, futureTime, NotificationStatus.PENDING);
        fixtureGenerator.generateNotification(mate, futureTime.plusHours(1), NotificationStatus.PENDING);

        entityManager.flush();
        entityManager.clear();

        // When
        notificationRepository.updateAllStatusToDismissedByMateIdAndSendAtAfterNow(mate.getId(), now);

        // Then
        List<Notification> notifications = notificationRepository.findAllByMateId(mate.getId());
        assertThat(notifications).hasSize(2);
        notifications.stream()
                .filter(noti -> noti.getSendAt().isAfter(now))
                .forEach(noti -> assertThat(noti.getStatus()).isEqualTo(NotificationStatus.DISMISSED));
        notifications.stream()
                .filter(noti -> noti.getSendAt().isBefore(now))
                .forEach(noti -> assertThat(noti.getStatus()).isEqualTo(NotificationStatus.PENDING));
    }
}