package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.EncryptionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class EncryptionExceptionTest {

    @Test
    void encryptionExceptionMessage() {
        Throwable cause = new RuntimeException("Encryption failed");

        EncryptionException exception = assertThrows(
                EncryptionException.class,
                () -> { throw new EncryptionException(cause); }
        );

        assertSame(cause, exception.getCause());
    }
}
