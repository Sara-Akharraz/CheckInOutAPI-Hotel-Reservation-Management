package com.api.apicheck_incheck_out.Mapper;

import com.api.apicheck_incheck_out.DTO.NotificationDTO;
import com.api.apicheck_incheck_out.Entity.Notification;
import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    @Autowired
    private UserRepository userRepository;

    public NotificationDTO toDTO(Notification notif){
        return new NotificationDTO(
                notif.getId(),
                notif.getMessage(),
                notif.getDateEnvoi(),
                notif.getUser().getId()
        );
    }

    public Notification toEntity(NotificationDTO notificationDTO){
        User user=userRepository.findById(notificationDTO.getUserId()).orElseThrow(()->new RuntimeException("User introuvable"));
        return new Notification(
              notificationDTO.getId(),
              notificationDTO.getMessage(),
              notificationDTO.getDateEnvoi(),
              user
        );
    }
}
