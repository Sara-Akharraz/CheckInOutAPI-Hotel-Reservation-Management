package com.api.apicheck_incheck_out.Stripe.Service.Impl;

import com.api.apicheck_incheck_out.Stripe.Service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.api.secretKey}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        System.out.println("Stripe API Key configured: " + stripeApiKey);
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public boolean processPayment(double amountInMAD, String customerId, String currency) {
        double amountusd =amountInMAD/10;
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long)(amountusd * 100))
                    .setCurrency(currency.toLowerCase())
                    .setCustomer(customerId)
                    .setConfirm(true)
                    .setPaymentMethod("pm_card_visa")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);


            System.out.println("Status du paiement StripeMock: " + paymentIntent.getStatus());

            return "succeeded".equals(paymentIntent.getStatus()) || "requires_payment_method".equals(paymentIntent.getStatus());
        } catch(StripeException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public PaymentIntent createPaymentIntent(Double amount) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", (int)(amount * 100));
        params.put("currency", "mad");
        params.put("payment_method_types", List.of("card"));

        return PaymentIntent.create(params);
    }
}
