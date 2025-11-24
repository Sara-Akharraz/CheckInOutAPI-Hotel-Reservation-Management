package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.InvalidReservationStatusException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class InvalidReservationStatusExceptionTest {
    @Test
    void testInvalidReservationStatusException(){
        InvalidReservationStatusException exception=new InvalidReservationStatusException("erreur");
        assertEquals("erreur",exception.getMessage());
    }
}
