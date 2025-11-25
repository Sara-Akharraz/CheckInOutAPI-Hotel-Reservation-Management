package com.api.apicheck_incheck_out.service;

import com.api.apicheck_incheck_out.entity.CheckOut;
import com.api.apicheck_incheck_out.enums.CheckOutStatut;
import com.api.apicheck_incheck_out.stripe.StripeResponse;

import java.time.LocalDate;
import java.util.List;

    public interface CheckOutService {
        public CheckOut addCheckOut(CheckOut checkout);
        public CheckOut getCheckOutById(Long id);
        public List<CheckOut> getAllCheckOuts();
        public CheckOut setCheckOutStatus(Long id, CheckOutStatut newStatus);
        public double getAmount(Long id);
        public StripeResponse payer(Long id);
        public CheckOut getCheckOutByReservation(Long idReservation);
        public void handlePaymentSuccess(Long id);
        public List<CheckOut> checkoutsForToday(LocalDate today);
    }
