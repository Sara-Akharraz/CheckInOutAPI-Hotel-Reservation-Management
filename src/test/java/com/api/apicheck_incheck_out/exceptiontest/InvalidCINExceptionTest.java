package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.InvalidCINException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidCINExceptionTest {
    @Test
    void testInvalidCINException(){
        InvalidCINException exception=new InvalidCINException("erreur");
        assertEquals("erreur",exception.getMessage());
    }
}
