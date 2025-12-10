package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.stripe.service.impl.StripeServiceImpl;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StripeServiceTest {
    StripeServiceImpl stripeService = new StripeServiceImpl();

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


}
