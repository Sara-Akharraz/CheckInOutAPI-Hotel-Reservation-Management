package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.entity.Notification;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.exceptionhandling.NotificationNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.UserNotFoundException;
import com.api.apicheck_incheck_out.repository.NotificationRepository;
import com.api.apicheck_incheck_out.repository.UserRepository;
import com.api.apicheck_incheck_out.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Notification notifier(Long userId, String message) {

        Optional<User> user=userRepository.findById(userId);
        if(user.isPresent()){
            User user1=user.get();
            Notification notif=new Notification();
            notif.setMessage(message);
            notif.setDateEnvoi(LocalDate.now());
            notif.setUser(user1);
            return notificationRepository.save(notif);
        }else{
            throw new UserNotFoundException("Client introuvable pour l'id "+ userId);
        }

    }

    @Override
    public List<Notification> getAllNotificationsByUser(Long id) {
        return notificationRepository.findByUserId(id);
    }

    @Override
    public void deleteNotification(Long id) {
        Optional<Notification> notification=notificationRepository.findById(id);
        if(notification.isPresent()){
            notificationRepository.deleteById(id);

        }else{
            throw new NotificationNotFoundException("Notification introuvable pour l'id "+id);
        }

    }
}
