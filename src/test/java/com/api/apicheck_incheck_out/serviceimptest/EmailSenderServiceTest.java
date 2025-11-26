package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.service.impl.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

 class EmailSenderServiceTest {
    private JavaMailSender mailSender;
    private EmailSenderService emailSenderService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailSenderService = new EmailSenderService(mailSender);


        try {
            java.lang.reflect.Field field = EmailSenderService.class.getDeclaredField("senderEmail");
            field.setAccessible(true);
            field.set(emailSenderService, "noreply@example.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSendEmail() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Hello, this is a test email.";

        emailSenderService.sendEmail(to, subject, body);


        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertEquals("noreply@example.com", sentMessage.getFrom());
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }
}
