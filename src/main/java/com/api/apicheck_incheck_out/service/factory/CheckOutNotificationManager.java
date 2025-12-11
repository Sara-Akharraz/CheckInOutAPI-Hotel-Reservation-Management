package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.service.NotificationService;
import com.api.apicheck_incheck_out.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckOutNotificationManager {

    NotificationService notificationService;
    UserService userService;

    String message = "Paiement Confirmé pour la réservation numéro : ";

    public void paymentConfirmed(Long idUser, Long idReservation){
        notificationService.notifier(idUser,message+ idReservation);
        List<User> admins = userService.getAdmins();
        admins.stream().forEach(admin ->
                notificationService.notifier(admin.getId(),message+idReservation));
        List<UserDto> receps = userService.getReceptionists();
        receps.stream().forEach(recep ->
                notificationService.notifier(recep.getId(),message+ idReservation)
        );
    }
}
