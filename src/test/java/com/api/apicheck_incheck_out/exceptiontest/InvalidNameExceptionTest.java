package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.InvalidNameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidNameExceptionTest {
    @Test
    void testInvalidNameException(){
        InvalidNameException exception=new InvalidNameException("erreur");
        assertEquals("erreur",exception.getMessage());
    }
}
