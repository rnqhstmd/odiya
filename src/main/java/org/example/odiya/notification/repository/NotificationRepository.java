package org.example.odiya.notification.repository;

import org.example.odiya.notification.domain.Notification;
import org.example.odiya.notification.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT noti" +
            " FROM Notification noti" +
            " JOIN FETCH Mate mate on noti.mate.id = mate.id and mate.meeting.id = :meetingId" +
            " JOIN FETCH Member member on mate.member.id = member.id" +
            " WHERE noti.type = :type")
    List<Notification> findAllMeetingIdAndType(@Param("meetingId") Long meetingId, @Param("type") NotificationType type);

    @Query("UPDATE Notification noti" +
            " SET noti.status = 'DISMISSED'" +
            " WHERE noti.mate.id = :mateId " +
            " AND noti.sendAt > :now")
    @Modifying(clearAutomatically = true)
    void updateAllStatusToDismissedByMateIdAndSendAtAfterNow(long mateId, LocalDateTime now);

    List<Notification> findAllByMateId(Long mateId);
}
