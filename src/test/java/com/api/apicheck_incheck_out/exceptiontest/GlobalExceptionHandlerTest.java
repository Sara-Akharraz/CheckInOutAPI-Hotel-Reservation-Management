package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.GlobalExceptionHandler;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleEntityNotFoundException() {

        String messageErreur = "Entité non trouvée";
        EntityNotFoundException exception = new EntityNotFoundException(messageErreur);

        ResponseEntity<String> response = globalExceptionHandler.handleEntityNotFoundException(exception);

        assertNotNull(response, "La réponse ne devrait pas être null");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "Le status HTTP devrait être NOT_FOUND (404)");
        assertEquals(messageErreur, response.getBody(),
                "Le message d'erreur devrait correspondre");
    }

    @Test
    void testHandleEntityExistsException() {

        String messageErreur = "Entité existe déjà";
        EntityExistsException exception = new EntityExistsException(messageErreur);

        ResponseEntity<String> response = globalExceptionHandler.handleEntityExistsException(exception);

        assertNotNull(response, "La réponse ne devrait pas être null");
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(),
                "Le status HTTP devrait être CONFLICT (409)");
        assertEquals(messageErreur, response.getBody(),
                "Le message d'erreur devrait correspondre");
    }
}
