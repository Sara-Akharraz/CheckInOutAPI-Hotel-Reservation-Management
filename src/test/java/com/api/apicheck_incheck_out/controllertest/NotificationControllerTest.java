package com.api.apicheck_incheck_out.controllertest;

import com.api.apicheck_incheck_out.controller.NotificationController;
import com.api.apicheck_incheck_out.dto.NotificationDTO;
import com.api.apicheck_incheck_out.entity.Notification;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.mapper.NotificationMapper;
import com.api.apicheck_incheck_out.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class NotificationControllerTest {
    @Mock
    NotificationService notificationService;
    @InjectMocks
    NotificationController notificationController;
    @Mock
    NotificationMapper notificationMapper;

    @Test
    void testNotifier_success(){
        NotificationDTO dto=new NotificationDTO();
        dto.setUserId(1L);
        dto.setMessage("free Palestine");

        Notification notification=new Notification();
        User user=new User();
        user.setId(1L);
        notification.setUser(user);
        notification.setMessage("free Palestine");

        when(notificationService.notifier(dto.getUserId(),dto.getMessage())).thenReturn(notification);

        when(notificationMapper.toDTO(notification)).thenReturn(dto);

        ResponseEntity<NotificationDTO> response=notificationController.notifier(dto);

        assertEquals(HttpStatus.CREATED,response.getStatusCode());
        assertEquals(dto,response.getBody());

    }
    @Test
    void testNotifierThrowsException(){
        NotificationDTO dto=new NotificationDTO();
        dto.setUserId(1L);
        dto.setMessage("free Palestine");

        when(notificationService.notifier(dto.getUserId(), dto.getMessage()))
                .thenThrow(new RuntimeException("User not found"));

        ResponseEntity<NotificationDTO> response=notificationController.notifier(dto);

        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteNotification(){
        Long id=1L;
        doNothing().when(notificationService).deleteNotification(id);
        ResponseEntity<Void> response=notificationController.deleteNotification(id);
        assertEquals(HttpStatus.NO_CONTENT,response.getStatusCode());
    }
    @Test
    void testGetAllNotificationsByUser(){
        Long id=1L;
        Notification notification1 = new Notification();
        notification1.setId(1L);
        notification1.setMessage("Message 1");
        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setMessage("Message 2");

        NotificationDTO dto1 = new NotificationDTO();
        dto1.setId(1L);
        dto1.setMessage("Message 1");
        NotificationDTO dto2 = new NotificationDTO();
        dto2.setId(2L);
        dto2.setMessage("Message 2");

        List<Notification> notifications = List.of(notification1, notification2);
        List<NotificationDTO> dtos = List.of(dto1, dto2);

        when(notificationService.getAllNotificationsByUser(id)).thenReturn(notifications);
        when(notificationMapper.toDTO(notification1)).thenReturn(dto1);
        when(notificationMapper.toDTO(notification2)).thenReturn(dto2);

        ResponseEntity<List<NotificationDTO>> response = notificationController.getAllNotificationsByUser(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());
    }

}
