package com.api.apicheck_incheck_out.ServiceImpTest;

import com.api.apicheck_incheck_out.Entity.Notification;
import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Repository.NotificationRepository;
import com.api.apicheck_incheck_out.Repository.UserRepository;
import com.api.apicheck_incheck_out.Service.Impl.EmailSenderService;
import com.api.apicheck_incheck_out.Service.Impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private NotificationServiceImpl notificationService;
    private Notification notification;


    private User user;
    @BeforeEach
    public void setup(){
         user=new User();
         user.setId(1L);
         user.setNom("aman");
         user.setPassword("123");
         notification=new Notification(1L,"test notif", LocalDate.now(),user);
    }
    @Test
    public void notifier() {
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
    public void getAllNotificationByUser(){
        List<Notification> notificationList= Arrays.asList(notification,
                new Notification(2L,"test notif 2",LocalDate.now().plusDays(2),user));
        when(notificationRepository.findByUserId(user.getId())).thenReturn(notificationList);

        List<Notification> result=notificationService.getAllNotificationsByUser(user.getId());
        assertNotNull(result);
        assertEquals(2,result.size());
        assertEquals("test notif",result.get(0).getMessage());
    }
    @Test
    public void deleteNotification(){
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        notificationService.deleteNotification(notification.getId());
    }
}
