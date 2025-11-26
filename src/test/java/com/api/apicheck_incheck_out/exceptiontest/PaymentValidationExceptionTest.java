package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.PaymentValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class PaymentValidationExceptionTest {
    @Test
     void testPaymentValidationException(){
        PaymentValidationException exception=new PaymentValidationException("erreur");
        assertEquals("erreur",exception.getMessage());
    }
}
