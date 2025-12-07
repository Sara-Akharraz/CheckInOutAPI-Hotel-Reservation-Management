package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.enums.CheckInStatus;
import com.api.apicheck_incheck_out.exceptionhandling.DocumentNotScannedException;
import com.api.apicheck_incheck_out.exceptionhandling.InvalidCheckInStatusException;
import com.api.apicheck_incheck_out.service.factory.CheckInValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
 class CheckInValidatorTest {
    @InjectMocks
    private CheckInValidator validator;
    @Test
    void testValidateForConfirmation_Success() {
        CheckIn checkIn = new CheckIn();
        checkIn.setDocumentScan(new DocumentScan());
        checkIn.setStatus(CheckInStatus.EN_ATTENTE);

        assertDoesNotThrow(() -> validator.validateForConfirmation(checkIn));
    }

    @Test
    void testValidateForConfirmation_DocumentNotScanned() {
        CheckIn checkIn = new CheckIn();
        checkIn.setDocumentScan(null);
        checkIn.setStatus(CheckInStatus.EN_ATTENTE);

        DocumentNotScannedException ex = assertThrows(DocumentNotScannedException.class,
                () -> validator.validateForConfirmation(checkIn));
        assertEquals("Le scan du document n'a pas été effectué.", ex.getMessage());
    }

    @Test
    void testValidateForConfirmation_InvalidStatus() {
        CheckIn checkIn = new CheckIn();
        checkIn.setDocumentScan(new DocumentScan());
        checkIn.setStatus(CheckInStatus.VALIDE);

        InvalidCheckInStatusException ex = assertThrows(InvalidCheckInStatusException.class,
                () -> validator.validateForConfirmation(checkIn));
        assertEquals("Le check-in n'est pas en attente.", ex.getMessage());
    }
}
