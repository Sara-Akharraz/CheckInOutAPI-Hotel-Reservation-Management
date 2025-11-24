package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.UserRegistrationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class UserRegistrationExceptionTest {
    @Test
    void testUserRegistrationException(){
        Throwable cause=new RuntimeException("cause");

        UserRegistrationException exception=new UserRegistrationException("erreur",cause);
        assertEquals("erreur",exception.getMessage());
        assertEquals(cause,exception.getCause());
    }
}
