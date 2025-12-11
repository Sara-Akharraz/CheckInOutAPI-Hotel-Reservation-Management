package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.exceptionhandling.CheckOutNotFoundException;
import com.api.apicheck_incheck_out.stripe.CheckOutRequest;
import com.api.apicheck_incheck_out.stripe.StripeResponse;
import com.api.apicheck_incheck_out.stripe.service.StripeService;
import com.api.apicheck_incheck_out.stripe.service.impl.StripeServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StripeServiceTest {

    @InjectMocks
    StripeServiceImpl stripeService;

    @Test
    void testSuccessPayment() throws Exception {

        PaymentIntent mockIntent = mock(PaymentIntent.class);
        when(mockIntent.getStatus()).thenReturn("succeeded");

        try (MockedStatic<PaymentIntent> mockedStatic = mockStatic(PaymentIntent.class)) {

            mockedStatic
                    .when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(mockIntent);

            boolean result = stripeService.processPayment(1000, "1234", "MAD");

            assertTrue(result);

        }
    }

    @Test
    void testNotSuccessPaymentException(){
        StripeException stripeException = mock(StripeException.class);

        try (MockedStatic<PaymentIntent> mockedStatic = mockStatic(PaymentIntent.class)) {

            mockedStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenThrow(stripeException);

            boolean result = stripeService.processPayment(100, "cus_123", "usd");

            assertFalse(result);
        }
    }

    @Test
    void testCreatePaymentIntentSuccess() throws StripeException {
        PaymentIntent mockIntent = mock(PaymentIntent.class);

        try (MockedStatic<PaymentIntent> mockedStatic = mockStatic(PaymentIntent.class)) {
            mockedStatic.when(() -> PaymentIntent.create(any(Map.class))).thenReturn(mockIntent);

            PaymentIntent result = stripeService.createPaymentIntent(100.0);

            assertNotNull(result);
            assertEquals(mockIntent, result);
        }
    }

    @Test
    void testCheckoutServicesSuccess() throws StripeException {
        stripeService.setStripeApiKey("sk_test_123");

        CheckOutRequest request = CheckOutRequest.builder()
                .checkOutName("Test Service")
                .amount(5000L)
                .currency("MAD")
                .quantity(2L)
                .idCheckQOut(123L)
                .build();

        Session mockSession = mock(Session.class);
        when(mockSession.getId()).thenReturn("sess_123");
        when(mockSession.getUrl()).thenReturn("http://stripe.com/checkout");

        try (MockedStatic<Session> mockedStatic = mockStatic(Session.class)) {
            mockedStatic.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(mockSession);

            StripeResponse response = stripeService.checkoutServices(request);

            assertEquals("SUCCESS", response.getStatus());
            assertEquals("sess_123", response.getSessionId());
            assertEquals("http://stripe.com/checkout", response.getSessionUrl());
        }
    }

    @Test
    void testCheckoutServicesStripeException() throws StripeException {
        stripeService.setStripeApiKey("sk_test_123");

        CheckOutRequest request = CheckOutRequest.builder()
                .amount(5000L)
                .idCheckQOut(123L)
                .build();

        StripeException stripeException = mock(StripeException.class);

        try (MockedStatic<Session> mockedStatic = mockStatic(Session.class)) {
            mockedStatic.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenThrow(stripeException);

            StripeResponse response = stripeService.checkoutServices(request);

            assertEquals("ERROR", response.getStatus());
        }
    }
}
