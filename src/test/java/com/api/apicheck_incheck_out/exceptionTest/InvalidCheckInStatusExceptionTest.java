package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.InvalidCheckInStatusException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class InvalidCheckInStatusExceptionTest {
    @Test
    void invalidCheckInStatusExceptionMessage() {
            String message = "Invalid Check-In Status";

        InvalidCheckInStatusException exception = assertThrows(
                InvalidCheckInStatusException.class,
                () -> { throw new InvalidCheckInStatusException(message); }
        );

        assertEquals(message, exception.getMessage());
    }
}
