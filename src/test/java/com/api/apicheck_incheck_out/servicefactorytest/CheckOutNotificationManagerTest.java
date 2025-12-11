package com.api.apicheck_incheck_out.servicefactorytest;


import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.Notification;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.enums.Role;
import com.api.apicheck_incheck_out.service.NotificationService;
import com.api.apicheck_incheck_out.service.UserService;
import com.api.apicheck_incheck_out.service.factory.CheckOutNotificationManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckOutNotificationManagerTest {

    @Mock
    NotificationService notificationService;

    @Mock
    UserService userService;

    @InjectMocks
    CheckOutNotificationManager checkOutNotificationManager;

    @Test
    void PaymentConfirmed(){
        User user = User.builder()
                .id(1L)
                .role(Role.ADMIN)
                .build();
        UserDto user1 = UserDto.builder()
                .id(2L)
                .role(Role.RECEPTIONIST)
                .build();

        Notification notification = new Notification();
        notification.setId(1L);

        when(userService.getAdmins()).thenReturn(List.of(user));
        when(userService.getReceptionists()).thenReturn(List.of(user1));
        when(notificationService.notifier(anyLong(),anyString())).thenReturn(notification);

        checkOutNotificationManager.paymentConfirmed(1L,1L);

        String message = "Paiement Confirmé pour la réservation numéro : "+1;

        verify(notificationService,times(2)).notifier(user.getId(), message);

    }
}
