package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.CheckOut;
import com.api.apicheck_incheck_out.enums.CheckOutStatut;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.repository.CheckOutRepository;
import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CheckOutStatusManager {

    CheckOutFinder checkOutFinder;
    CheckOutRepository checkOutRepository;

    public CheckOut confirmed(Long id){
        CheckOut checkOut = checkOutFinder.findById(id);
        checkOut.setDateCheckOut(LocalDate.now());
        checkOut.setCheckOutStatut(CheckOutStatut.CONFIRMEE);
        checkOut.getReservation().setStatus(ReservationStatus.TERMINEE);
        return checkOutRepository.save(checkOut);
}
    public CheckOut newStatus(Long id, CheckOutStatut newStatus){
        CheckOut checkout = checkOutFinder.findById(id);
        checkout.setCheckOutStatut(newStatus);
        return checkOutRepository.save(checkout);
    }

    public CheckOut newWaitedCheckOut(CheckOut checkout){
        if (checkout == null) {
            throw new IllegalArgumentException("Check_Out object cannot be null");
        }
        checkout.setCheckOutStatut(CheckOutStatut.EN_ATTENTE);
        return checkOutRepository.save(checkout);
    }
}
