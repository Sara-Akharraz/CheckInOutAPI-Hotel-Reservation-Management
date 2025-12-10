package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.CheckOut;
import com.api.apicheck_incheck_out.enums.CheckOutStatut;
import com.api.apicheck_incheck_out.exceptionhandling.PaymentValidationException;
import com.api.apicheck_incheck_out.repository.CheckOutRepository;
import com.api.apicheck_incheck_out.service.CheckOutService;
import com.api.apicheck_incheck_out.service.factory.StripeResponseCreator;
import com.api.apicheck_incheck_out.stripe.CheckOutRequest;
import com.api.apicheck_incheck_out.stripe.StripeResponse;
import com.api.apicheck_incheck_out.stripe.service.StripeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StripeResponseCreatorTest {


    @Mock
    CheckOutRepository checkOutRepository;
    @Mock
    StripeService stripeService;
    @Mock
    CheckOutService checkOutService;
    CheckOut checkOut;
    @InjectMocks
    StripeResponseCreator stripeResponseCreator;
    @BeforeEach
    void setUp(){
         checkOut = CheckOut.builder()
                    .id(1L)
                    .dateCheckOut(LocalDate.of(2025, 11, 30))
                    .checkOutStatut(CheckOutStatut.EN_ATTENTE)
                    .build();
    }

    @Test
    void createTest(){
        StripeResponse stripeResponse = StripeResponse.builder()
                .status("SUCCESS")
                .message("SUCCESS")
                .sessionId("1111")
                .sessionUrl("session-url")
                .build();

        when(stripeService.checkoutServices(any(CheckOutRequest.class))).thenReturn(stripeResponse);

        StripeResponse returnedStripeResponse =  stripeResponseCreator.create(1L,180.0);

        assertNotNull(returnedStripeResponse);
        assertEquals(stripeResponse.getStatus(), returnedStripeResponse.getStatus());
        assertEquals(stripeResponse.getSessionId(), returnedStripeResponse.getSessionId());
        assertEquals(stripeResponse.getSessionUrl(), returnedStripeResponse.getSessionUrl());
    }
    @Test
    void payerTest_PaymentValidationException(){
        StripeResponse stripeResponse = null;
        when(stripeService.checkoutServices(any(CheckOutRequest.class))).thenReturn(stripeResponse);

        PaymentValidationException e = assertThrows(PaymentValidationException.class,
                () -> stripeResponseCreator.create(1L,180.0));

        assertEquals("Stripe payment failed",e.getMessage());
    }
}
