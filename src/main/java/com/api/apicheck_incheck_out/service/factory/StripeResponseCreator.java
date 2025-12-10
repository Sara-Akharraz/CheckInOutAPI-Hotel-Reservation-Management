package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.exceptionhandling.PaymentValidationException;
import com.api.apicheck_incheck_out.stripe.CheckOutRequest;
import com.api.apicheck_incheck_out.stripe.StripeResponse;
import com.api.apicheck_incheck_out.stripe.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StripeResponseCreator {

    private final StripeService stripeService;

    public StripeResponseCreator(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    public StripeResponse create(Long id, double amount) {
        StripeResponse stripeResponse = stripeService.checkoutServices(
                CheckOutRequest.builder()
                        .idCheckQOut(id)
                        .checkOutName("Services consommés pendant le séjour")
                        .amount(Math.round(amount * 100))
                        .build());
        if (stripeResponse == null || !"SUCCESS".equals(stripeResponse.getStatus()))
            throw new PaymentValidationException("Stripe payment failed");

        return stripeResponse;
    }
}
