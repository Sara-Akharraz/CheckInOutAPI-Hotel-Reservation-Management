package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.entity.Notification;
import com.api.apicheck_incheck_out.entity.User;

import com.api.apicheck_incheck_out.exceptionhandling.NotificationNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.UserNotFoundException;
import com.api.apicheck_incheck_out.repository.NotificationRepository;
import com.api.apicheck_incheck_out.repository.UserRepository;
import com.api.apicheck_incheck_out.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class NotificationServiceImplTest {
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private NotificationServiceImpl notificationService;
    private Notification notification;


    private User user;
    @BeforeEach
     void setup(){
         user=new User();
         user.setId(1L);
         user.setNom("aman");
         user.setPassword("123");
         notification=new Notification(1L,"test notif", LocalDate.now(),user);
    }
    @Test
     void notifier() {
        Long userId = user.getId();
        String message = "test notif";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notifArg = invocation.getArgument(0);
            notifArg.setId(1L);
            return notifArg;
        });

        Notification createdNotif = notificationService.notifier(userId, message);

        assertNotNull(createdNotif);
        assertEquals(message, createdNotif.getMessage());
        assertEquals(user, createdNotif.getUser());
    }

    @Test
    void notifierThrowsException(){
        String message = "test notif";
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        UserNotFoundException ex=assertThrows(UserNotFoundException.class,()->notificationService.notifier(2L,message));
        assertEquals("Client introuvable pour l'id 2",ex.getMessage());
        verify(userRepository,times(1)).findById(2L);
        verify(notificationRepository,never()).save(any(Notification.class));
    }

    @Test
     void getAllNotificationByUser(){
        List<Notification> notificationList= Arrays.asList(notification,
                new Notification(2L,"test notif 2",LocalDate.now().plusDays(2),user));
        when(notificationRepository.findByUserId(user.getId())).thenReturn(notificationList);

        List<Notification> result=notificationService.getAllNotificationsByUser(user.getId());
        assertNotNull(result);
        assertEquals(2,result.size());
        assertEquals("test notif",result.get(0).getMessage());
    }
    @Test

     void deleteNotification(){
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        notificationService.deleteNotification(notification.getId());
    }
    @Test
    void deleteNotificationThrowsException(){
        when(notificationRepository.findById(2L)).thenReturn(Optional.empty());
        NotificationNotFoundException ex=assertThrows(NotificationNotFoundException.class,()->notificationService.deleteNotification(2L));

        assertEquals("Notification introuvable pour l'id 2",ex.getMessage());
        verify(notificationRepository,times(1)).findById(2L);
        verify(notificationRepository,never()).deleteById(2L);
    }


}
