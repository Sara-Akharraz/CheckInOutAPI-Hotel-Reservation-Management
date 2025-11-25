package com.api.apicheck_incheck_out.exceptionTest;

import com.api.apicheck_incheck_out.exceptionhandling.EmailAlreadyUsedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailAlreadyUsedExceptionTest {
    @Test
    void emailAlreadyUsedExceptionMessage() {
        String message = "Email already used by another user";

        EmailAlreadyUsedException exception = assertThrows(
                EmailAlreadyUsedException.class,
                () -> { throw new EmailAlreadyUsedException(message); }
        );

        assertEquals(message, exception.getMessage());
    }
}
