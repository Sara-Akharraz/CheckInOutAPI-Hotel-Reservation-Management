package com.api.apicheck_incheck_out.exceptionTest;

import com.api.apicheck_incheck_out.exceptionhandling.ChambreNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.CheckOutNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.EncryptionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptionExceptionTest {

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
