package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.InvalidPassportException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidPassportExceptionTest {
    @Test
    void testInvalidPassportException(){
        InvalidPassportException exception=new InvalidPassportException("erreur");
        assertEquals("erreur",exception.getMessage());
    }
}
