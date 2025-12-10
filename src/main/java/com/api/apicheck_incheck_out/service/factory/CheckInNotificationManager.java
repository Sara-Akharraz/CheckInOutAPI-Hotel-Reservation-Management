package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.service.NotificationService;
import com.api.apicheck_incheck_out.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckInNotificationManager {
    private final NotificationService notificationService;
    private final UserService userService;

    public static final String CHECKIN_VALID = "Check-In validé pour la réservation numéro : ";
    public static final String CHECKIN_ADDED = "Check-In ajouté pour la réservation numéro : ";

    public CheckInNotificationManager(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    public void notifyUserCheckInPending(Long userId) {
        notificationService.notifier(userId,
                "Votre check-in est en attente. Merci de procéder au paiement demandé.");
    }

    public void notifyUserReservationConfirmed(Long userId, Long reservationId) {
        notificationService.notifier(userId,
                "Réservation Confirmée ,Numéro de reservation :" + reservationId);
    }

    public void notifyStaffCheckInValidated(Long reservationId) {
        notifyAdmins(CHECKIN_VALID + reservationId);
        notifyReceptionists(CHECKIN_VALID + reservationId);
    }

    public void notifyStaffCheckInAdded(Long reservationId) {
        notifyAdmins(CHECKIN_ADDED + reservationId);
        notifyReceptionists(CHECKIN_ADDED + reservationId);
    }

    private void notifyAdmins(String message) {
        List<User> admins = userService.getAdmins();
        admins.forEach(admin -> notificationService.notifier(admin.getId(), message));
    }

    private void notifyReceptionists(String message) {
        List<UserDto> receptionists = userService.getReceptionists();
        receptionists.forEach(recep -> notificationService.notifier(recep.getId(), message));
    }
}
