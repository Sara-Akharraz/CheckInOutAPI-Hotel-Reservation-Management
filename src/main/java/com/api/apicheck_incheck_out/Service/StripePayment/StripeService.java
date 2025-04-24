package com.api.apicheck_incheck_out.Service.StripePayment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    private String secretKey="sk_test_51RC5OCQDfYoFTDhzcSOluEJfLGBgl2NwOSLSEImwTfIskU53Shjonb0wvmPGfrPcbv8vjGXjLOKc3EimnT0u2GJy00j0k3EhCs";
   public StripeResponse checkoutServices(CheckOutRequest checkOutRequest) {
       Stripe.apiKey=secretKey;
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
//               .setSuccessUrl("http://localhost:8080/checkout/payment/success")
               .setCancelUrl("http://localhost:8080/checkout/payment")
               .addLineItem(lineItem)
               .build();
       Session session = null;

       try {
           // Attempt to create the session
           session = Session.create(params);
       } catch (StripeException e) {
           // Log detailed information about the error for debugging
           System.err.println("Error creating session: " + e.getMessage());
           e.printStackTrace();
           // Return an error response
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
           // Return an error response if session creation failed
           return StripeResponse.builder()
                   .status("ERROR")
                   .message("Session creation failed, session is null")
                   .build();
       }
   }
}
