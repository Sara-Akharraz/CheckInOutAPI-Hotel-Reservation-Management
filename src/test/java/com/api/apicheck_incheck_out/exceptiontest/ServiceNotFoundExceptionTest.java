package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.ServiceNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class ServiceNotFoundExceptionTest {
    @Test
    void testServiceNotFoundException(){
        ServiceNotFoundException exception=new ServiceNotFoundException("erreur");
        assertEquals("erreur",exception.getMessage());

    }
}
