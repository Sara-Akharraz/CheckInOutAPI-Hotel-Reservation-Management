package com.api.apicheck_incheck_out.Stripe.Service.Impl;

import com.api.apicheck_incheck_out.Stripe.CheckOutRequest;
import com.api.apicheck_incheck_out.Stripe.Service.StripeService;
import com.api.apicheck_incheck_out.Stripe.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
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
    @Override
    public StripeResponse checkoutServices(CheckOutRequest checkOutRequest) {
        Stripe.apiKey=stripeApiKey;
        SessionCreateParams.LineItem.PriceData.ProductData checkOutData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(checkOutRequest.getCheckOutName()==null ? "Extras Services":checkOutRequest.getCheckOutName()).build();

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setUnitAmount(checkOutRequest.getAmount())
                .setProductData(checkOutData)
                .setCurrency(checkOutRequest.getCurrency() == null ? "MAD" : checkOutRequest.getCurrency())
                .build();
        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(checkOutRequest.getQuantity()==null ? 1L :checkOutRequest.getQuantity())
                .setPriceData(priceData)
                .build();
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/checkout-payment-success/"+checkOutRequest.getIdCheckQOut())
                .setCancelUrl("http://localhost:3000/checkout-payment-failed")
                .addLineItem(lineItem)
                .build();
        Session session = null;

        try {
            session = Session.create(params);
        } catch (StripeException e) {
            System.err.println("Error creating session: " + e.getMessage());
            e.printStackTrace();
            return StripeResponse.builder()
                    .status("ERROR")
                    .message("Failed to create Stripe session: " + e.getMessage())
                    .build();
        }

        // If session creation is successful, return the response
        if (session != null) {
            return StripeResponse.builder()
                    .status("SUCCESS")
                    .message("Payment Session created successfully")
                    .sessionId(session.getId())  // Session ID
                    .sessionUrl(session.getUrl())  // Session URL for redirection
                    .build();
        } else {
            return StripeResponse.builder()
                    .status("ERROR")
                    .message("Session creation failed, session is null")
                    .build();
        }
    }
}
