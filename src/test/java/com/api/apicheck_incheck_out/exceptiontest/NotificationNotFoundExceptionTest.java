package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.NotificationNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class NotificationNotFoundExceptionTest {
    @Test
    void testNotificationNotFoundException(){
        NotificationNotFoundException exception=new NotificationNotFoundException("erreur");
        assertEquals("erreur",exception.getMessage());
    }
}
