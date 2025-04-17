package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Entity.Notification;
import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Repository.NotificationRepository;
import com.api.apicheck_incheck_out.Repository.UserRepository;
import com.api.apicheck_incheck_out.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
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
            throw new RuntimeException("Client introuvable pour l'id "+ userId);
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
            throw new RuntimeException("Notification introuvable pour l'id "+id);
        }

    }
}
