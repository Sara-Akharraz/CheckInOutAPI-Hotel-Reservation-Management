package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.service.NotificationService;
import com.api.apicheck_incheck_out.service.UserService;
import com.api.apicheck_incheck_out.service.factory.CheckInNotificationManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class CheckInNotificationManagerTest {
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserService userService;
    @InjectMocks
    private CheckInNotificationManager manager;

    @Test
    void testNotifyUserCheckInPending() {
        manager.notifyUserCheckInPending(1L);

        verify(notificationService, times(1))
                .notifier(1L, "Votre check-in est en attente. Merci de procéder au paiement demandé.");
    }

    @Test
    void testNotifyUserReservationConfirmed() {
        manager.notifyUserReservationConfirmed(2L, 100L);

        verify(notificationService, times(1))
                .notifier(2L, "Réservation Confirmée ,Numéro de reservation :100");
    }

    @Test
    void testNotifyStaffCheckInValidated() {
        User admin = new User();
        admin.setId(10L);

        UserDto recep = new UserDto();
        recep.setId(20L);

        when(userService.getAdmins()).thenReturn(List.of(admin));
        when(userService.getReceptionists()).thenReturn(List.of(recep));

        manager.notifyStaffCheckInValidated(5L);

        verify(notificationService).notifier(10L, "Check-In validé pour la réservation numéro : 5");
        verify(notificationService).notifier(20L, "Check-In validé pour la réservation numéro : 5");
    }

    @Test
    void testNotifyStaffCheckInAdded() {
        User admin = new User();
        admin.setId(11L);

        UserDto recep = new UserDto();
        recep.setId(21L);

        when(userService.getAdmins()).thenReturn(List.of(admin));
        when(userService.getReceptionists()).thenReturn(List.of(recep));

        manager.notifyStaffCheckInAdded(7L);

        verify(notificationService).notifier(11L, "Check-In ajouté pour la réservation numéro : 7");
        verify(notificationService).notifier(21L, "Check-In ajouté pour la réservation numéro : 7");
    }
}
