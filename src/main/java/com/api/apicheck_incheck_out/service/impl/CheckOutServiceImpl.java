package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.CheckOutStatut;
import com.api.apicheck_incheck_out.exceptionhandling.PaymentValidationException;
import com.api.apicheck_incheck_out.repository.CheckOutRepository;
import com.api.apicheck_incheck_out.service.*;
import com.api.apicheck_incheck_out.service.factory.*;
import com.api.apicheck_incheck_out.stripe.CheckOutRequest;
import com.api.apicheck_incheck_out.stripe.StripeResponse;
import com.api.apicheck_incheck_out.stripe.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckOutServiceImpl implements CheckOutService {

    @Lazy
    private final FactureService factureService;
    @Autowired
    private final CheckOutRepository checkOutRepository;


    @Autowired
    private final CheckOutFinder checkOutFinder;
    @Autowired
    private final CheckOutStatusManager checkOutStatusManager;
    @Autowired
    private final ServicesPaymentManager servicesPaymentManager;
    @Autowired
    private final CheckOutNotificationManager checkOutNotificationManager;
    @Autowired
    private final StripeResponseCreator stripeResponseCreator;

    public CheckOut addCheckOut(CheckOut checkout) {
        return checkOutStatusManager.newWaitedCheckOut(checkout);
    }

    @Override
    public CheckOut getCheckOutById(Long id) {
        return checkOutFinder.findById(id);
    }

    @Override
    public List<CheckOut> getAllCheckOuts() {
        return checkOutRepository.findAll();
    }

    @Override
    public CheckOut setCheckOutStatus(Long id, CheckOutStatut newStatus) {
        return checkOutStatusManager.newStatus(id, newStatus);
    }

    @Override
    public double getAmount(Long id) {
        return servicesPaymentManager.totalPrice(id);
    }

    @Override
    public StripeResponse payer(Long id) {
        return stripeResponseCreator.create(id, getAmount(id));
    }

    @Override
    public void handlePaymentSuccess(Long id){
        CheckOut checkOut = checkOutStatusManager.confirmed(id);
        servicesPaymentManager.handleServicesPayment(checkOut.getReservation().getId());
        Reservation r = checkOut.getReservation();
        factureService.validerPaiementCheckOut(r,this.getAmount(checkOut.getId()));
        checkOutNotificationManager.paymentConfirmed(checkOut.getReservation().getUser().getId(),r.getId());
    }

    @Override
    public List<CheckOut> checkoutsForToday(LocalDate today) {
        return checkOutFinder.findCheckoutsForToday(today);
    }
    @Override
    public CheckOut getCheckOutByReservation(Long idReservation) {
        return checkOutFinder.findByReservation(idReservation);

    }
}