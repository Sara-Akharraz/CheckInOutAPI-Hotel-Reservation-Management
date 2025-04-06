package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.NotificationDTO;
import com.api.apicheck_incheck_out.Entity.Notification;
import com.api.apicheck_incheck_out.Mapper.NotificationMapper;
import com.api.apicheck_incheck_out.Service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationService notificationService, NotificationMapper notificationMapper) {
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
    }

    @PostMapping
    public ResponseEntity<NotificationDTO> notifier(@RequestBody NotificationDTO notificationDTO){
        Notification notification= notificationMapper.toEntity(notificationDTO);
       try {
           Notification newNotification = notificationService.notifier(notificationDTO.getUserId(), notification);
           return new ResponseEntity<>(notificationMapper.toDTO(newNotification), HttpStatus.CREATED);
       }catch(RuntimeException e){
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
       }
    }
    @GetMapping("/{id}")
    public ResponseEntity<List<NotificationDTO>> getAllNotificationsByUser(@PathVariable Long id){
        List<NotificationDTO> notificationList=notificationService.getAllNotificationsByUser(id).stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notificationList);

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id){
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
