package org.example.odiya.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.notification.domain.Notification;
import org.example.odiya.notification.domain.types.HurryUpNotification;
import org.example.odiya.notification.dto.request.HurryUpRequest;
import org.example.odiya.notification.dto.request.PushRequest;
import org.example.odiya.notification.repository.NotificationRepository;
import org.example.odiya.notification.service.fcm.FcmPublisher;
import org.example.odiya.notification.service.fcm.FcmPushSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmPublisher fcmPublisher;
    private final TaskScheduler taskScheduler;
    private final FcmPushSender fcmPushSender;

    @Transactional
    public void saveAndScheduleNotification(Notification notification) {
        Notification savedNotification = saveNotification(notification);
        scheduleNotification(savedNotification);
    }

    @Transactional
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    private void scheduleNotification(Notification notification) {
        LocalDateTime sendAt = notification.getSendAt();
        PushRequest pushRequest = new PushRequest(this, notification);
        taskScheduler.schedule(
                () -> fcmPublisher.publishWithTransaction(pushRequest),
                sendAt.atZone(ZoneId.systemDefault()).toInstant()
        );
        log.info("알림 스케줄링 - type: {}, 예약시간: {}", notification.getType(), sendAt);
    }

    public void updateStatusToDone(Notification notification) {
        if (notification.isReminder()) {
            notification.updateStatusToDone();
            log.info("알림 상태 업데이트 - id: {}, type: {}",
                    notification.getId(),
                    notification.getType());
        }
    }

    @Transactional
    public void sendHurryUpNotification(Mate mate, Notification notification) {
        HurryUpNotification hurryUpNotification = new HurryUpNotification(mate);
        Notification savedNotification = saveNotification(hurryUpNotification.toNotification());
        HurryUpRequest hurryUpRequest = new HurryUpRequest(this, mate, savedNotification);
        fcmPublisher.publishWithTransaction(hurryUpRequest);
    }
}
