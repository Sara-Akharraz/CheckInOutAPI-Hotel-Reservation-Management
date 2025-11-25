package com.api.apicheck_incheck_out.mapper;

import com.api.apicheck_incheck_out.dto.NotificationDTO;
import com.api.apicheck_incheck_out.entity.Notification;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    private final UserRepository userRepository;

    public NotificationMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
