package org.example.odiya.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.NotFoundException;
import org.example.odiya.notification.domain.Notification;
import org.example.odiya.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.odiya.common.exception.type.ErrorType.NOTIFICATION_NOT_FOUND_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public Notification findById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException(NOTIFICATION_NOT_FOUND_ERROR));
    }
}
