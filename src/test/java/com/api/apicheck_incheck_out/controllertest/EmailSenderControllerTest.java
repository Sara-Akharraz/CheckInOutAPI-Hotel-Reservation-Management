package com.api.apicheck_incheck_out.controllertest;

import com.api.apicheck_incheck_out.controller.EmailSenderController;
import com.api.apicheck_incheck_out.service.impl.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

class EmailSenderControllerTest {
    @Mock
    private EmailSenderService emailSenderService;
    @InjectMocks
    private EmailSenderController emailSenderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void sendEmailTest(){
        String to = "test@mail.com";
        String subject = "subject";
        String body = "body";

        doNothing().when(emailSenderService).sendEmail(to,subject,body);

        ResponseEntity<String> response=emailSenderController.sendEmail(to,subject,body);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Email envoyé à "+to,response.getBody());
    }
    @Test
    void sendEmailThrowsException(){
        String to = "test@mail.com";
        String subject = "subject";
        String body = "body";

        doThrow(new RuntimeException("Erreur envoi mail")).when(emailSenderService).sendEmail(to,subject,body);

        ResponseEntity<String> response=emailSenderController.sendEmail(to,subject,body);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
        assertTrue(response.getBody().contains("Erreur"));
    }

}
