package com.api.apicheck_incheck_out.stripe.service;

import com.api.apicheck_incheck_out.stripe.CheckOutRequest;
import com.api.apicheck_incheck_out.stripe.StripeResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeService {

    public boolean processPayment(double amount,String custmerId,String currency);
    public PaymentIntent createPaymentIntent(Double amount) throws StripeException;
    public StripeResponse checkoutServices(CheckOutRequest checkOutRequest);
}
