package com.api.apicheck_incheck_out.mappertest;

import com.api.apicheck_incheck_out.dto.NotificationDTO;
import com.api.apicheck_incheck_out.entity.Notification;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.mapper.NotificationMapper;
import com.api.apicheck_incheck_out.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationMapperTest {

    private UserRepository userRepository;
    private NotificationMapper mapper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        mapper = new NotificationMapper(userRepository);
    }

    @Test
    void toDTO_shouldMapEntityToDTO() {
        User user = new User();
        user.setId(1L);

        Notification notif = new Notification();
        notif.setId(10L);
        notif.setMessage("Test message");
        notif.setDateEnvoi(LocalDate.of(2025,11,25));
        notif.setUser(user);

        NotificationDTO dto = mapper.toDTO(notif);

        assertNotNull(dto);
        assertEquals(notif.getId(), dto.getId());
        assertEquals(notif.getMessage(), dto.getMessage());
        assertEquals(notif.getDateEnvoi(), dto.getDateEnvoi());
        assertEquals(user.getId(), dto.getUserId());
    }

    @Test
    void toEntity_shouldMapDTOToEntity() {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(10L);
        dto.setMessage("Test message");
        dto.setDateEnvoi(LocalDate.of(2025,11,25));
        dto.setUserId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Notification notif = mapper.toEntity(dto);

        assertNotNull(notif);
        assertEquals(dto.getId(), notif.getId());
        assertEquals(dto.getMessage(), notif.getMessage());
        assertEquals(dto.getDateEnvoi(), notif.getDateEnvoi());
        assertEquals(user, notif.getUser());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void toEntity_shouldThrow_whenUserNotFound() {
        NotificationDTO dto = new NotificationDTO();
        dto.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mapper.toEntity(dto));
        assertEquals("User introuvable", exception.getMessage());
    }
}
