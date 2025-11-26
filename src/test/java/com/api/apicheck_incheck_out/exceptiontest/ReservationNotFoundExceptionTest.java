package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationNotFoundExceptionTest {
    @Test
    void testReservationNotFoundException(){
        ReservationNotFoundException exception=new ReservationNotFoundException("erreur");
        assertEquals("erreur",exception.getMessage());

    }
}
