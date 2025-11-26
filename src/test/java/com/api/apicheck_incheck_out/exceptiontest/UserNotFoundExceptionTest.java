package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.UserNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class UserNotFoundExceptionTest {
    @Test
    void testUserNotFoundException(){
        UserNotFoundException exception=new UserNotFoundException("erreur");
        assertEquals("erreur",exception.getMessage());

    }
}
