package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.DocumentScanType;
import com.api.apicheck_incheck_out.exceptionhandling.InvalidCINException;
import com.api.apicheck_incheck_out.exceptionhandling.InvalidNameException;
import com.api.apicheck_incheck_out.exceptionhandling.InvalidPassportException;
import com.api.apicheck_incheck_out.service.factory.DocumentScanValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
 class DocumentScanValidatorTest {
    @InjectMocks
    private DocumentScanValidator validator;
    private Reservation reservation;
    private User user;
    @BeforeEach
    void setUp() {
        validator = new DocumentScanValidator();
        user = new User();
        user.setNom("Sara");
        user.setPrenom("Akharraz");
        user.setCin("AB123456");
        user.setNumeroPassport("P123456");

        reservation = new Reservation();
        reservation.setUser(user);
    }
    @Test
    void testValidCIN() {
        DocumentScanDTO doc = new DocumentScanDTO();
        doc.setNom("Sara");
        doc.setPrenom("Akharraz");
        doc.setCin("AB123456");
        doc.setType(DocumentScanType.CIN);

        assertDoesNotThrow(() -> validator.validateDocument(reservation, doc));
    }

    @Test
    void testInvalidName() {
        DocumentScanDTO doc = new DocumentScanDTO();
        doc.setNom("Wrong");
        doc.setPrenom("Name");
        doc.setCin("AB123456");
        doc.setType(DocumentScanType.CIN);

        assertThrows(InvalidNameException.class,
                () -> validator.validateDocument(reservation, doc));
    }

    @Test
    void testInvalidCIN() {
        DocumentScanDTO doc = new DocumentScanDTO();
        doc.setNom("Sara");
        doc.setPrenom("Akharraz");
        doc.setCin("WRONGCIN");
        doc.setType(DocumentScanType.CIN);

        assertThrows(InvalidCINException.class,
                () -> validator.validateDocument(reservation, doc));
    }

    @Test
    void testValidPassport() {
        DocumentScanDTO doc = new DocumentScanDTO();
        doc.setNom("Sara");
        doc.setPrenom("Akharraz");
        doc.setPassport("P123456");
        doc.setType(DocumentScanType.PASSPORT);

        assertDoesNotThrow(() -> validator.validateDocument(reservation, doc));
    }

    @Test
     void testInvalidPassport() {
        DocumentScanDTO doc = new DocumentScanDTO();
        doc.setNom("Sara");
        doc.setPrenom("Akharraz");
        doc.setPassport("WRONGPASS");
        doc.setType(DocumentScanType.PASSPORT);

        assertThrows(InvalidPassportException.class,
                () -> validator.validateDocument(reservation, doc));
    }
}
