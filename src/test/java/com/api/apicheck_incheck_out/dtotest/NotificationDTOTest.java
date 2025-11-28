package com.api.apicheck_incheck_out.dtotest;

import com.api.apicheck_incheck_out.dto.NotificationDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class NotificationDTOTest {
    @Test
    void testEqualsHashCodeToStringCanEqual() {
        new NotificationDTO(100L,"Free Palestine",LocalDate.of(2025,11,28),2L);
        NotificationDTO dto1 = NotificationDTO.builder()
                .id(1L)
                .message("Test message")
                .dateEnvoi(LocalDate.of(2025, 11, 28))
                .userId(100L)
                .build();

        NotificationDTO dto2 = NotificationDTO.builder()
                .id(1L)
                .message("Test message")
                .dateEnvoi(LocalDate.of(2025, 11, 28))
                .userId(100L)
                .build();

        NotificationDTO dto3 = NotificationDTO.builder()
                .id(2L)
                .message("Autre message")
                .dateEnvoi(LocalDate.of(2025, 11, 29))
                .userId(101L)
                .build();

        // equals
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);

        // hashCode
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());

        // toString
        String expected = "NotificationDTO(id=1, message=Test message, dateEnvoi=2025-11-28, userId=100)";
        assertEquals(expected, dto1.toString());

    }
}
