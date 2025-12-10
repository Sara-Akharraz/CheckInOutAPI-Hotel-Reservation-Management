package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.entity.Notification;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.exceptionhandling.NotificationNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.UserNotFoundException;
import com.api.apicheck_incheck_out.repository.NotificationRepository;
import com.api.apicheck_incheck_out.repository.UserRepository;
import com.api.apicheck_incheck_out.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    @Override
    public Notification notifier(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Client introuvable pour l'id " + userId));

        Notification notif=new Notification();
            notif.setMessage(message);
            notif.setDateEnvoi(LocalDate.now());
            notif.setUser(user);
            return notificationRepository.save(notif);
    }

    @Override
    public List<Notification> getAllNotificationsByUser(Long id) {
        return notificationRepository.findByUserId(id);
    }

    @Override
    public void deleteNotification(Long id) {
        notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification introuvable pour l'id " + id));
        notificationRepository.deleteById(id);

    }
}
