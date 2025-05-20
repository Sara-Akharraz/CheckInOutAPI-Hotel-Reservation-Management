package com.api.apicheck_incheck_out.Stripe.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeService {

    public boolean processPayment(double amount,String custmerId,String currency);
//    public PaymentIntent createPaymentIntent(double amount,String customerId,String currency)throws StripeException;
    public PaymentIntent createPaymentIntent(Double amount) throws StripeException;
}
