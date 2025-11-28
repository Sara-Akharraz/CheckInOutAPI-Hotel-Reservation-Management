package com.api.apicheck_incheck_out.controllertest;


import com.api.apicheck_incheck_out.controller.NotificationController;
import com.api.apicheck_incheck_out.dto.NotificationDTO;
import com.api.apicheck_incheck_out.entity.Notification;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.mapper.NotificationMapper;
import com.api.apicheck_incheck_out.security.JwtService;
import com.api.apicheck_incheck_out.service.NotificationService;
import com.api.apicheck_incheck_out.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class NotificationControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    User user;
    @MockBean
    private JwtService jwtService;
    @MockBean
    NotificationMapper notificationMapper;
    @MockBean
    NotificationService notificationService;
    Notification notification;
    NotificationDTO notificationDTO;
    @BeforeEach
    void setUp(){
        user = User.builder().id(1L).build();
        notification = Notification.builder()
                .id(1L)
                .message("Notification pour notifier l'utilisateur").build();
        notificationDTO  = NotificationDTO.builder()
                .id(user.getId())
                .userId(1L)
                .message("Notification pour notifier l'utilisateur")
                .build();
    }

    @Test
    void notifierTest() throws Exception{
        when(notificationService.notifier(user.getId(),notificationDTO.getMessage())).thenReturn(notification);
        when(notificationMapper.toDTO(any(Notification.class))).thenReturn(notificationDTO);

        mockMvc.perform(post("/api/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notificationDTO.getId()))
                .andExpect(jsonPath("$.message").value(notificationDTO.getMessage()));

        verify(notificationService, times(1)).notifier(user.getId(),notificationDTO.getMessage());
    }

    @Test
    void getAllNotificationsByUserTest() throws Exception{
        List<Notification> notifs = new ArrayList<>();
        notifs.add(notification);
        when(notificationService.getAllNotificationsByUser(user.getId())).thenReturn(notifs);
        when(notificationMapper.toDTO(any(Notification.class))).thenReturn(notificationDTO);

        mockMvc.perform(get("/api/notification/{id}",user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(notificationDTO.getId()))
                .andExpect(jsonPath("$[0].message").value(notificationDTO.getMessage()));

        verify(notificationService, times(1)).getAllNotificationsByUser(user.getId());
    }

    @Test
    void deleteNotificationTest() throws Exception {
        doNothing().when(notificationService).deleteNotification(1L);

        mockMvc.perform(delete("/api/notification/{id}", 1L));

        verify(notificationService, times(1)).deleteNotification(1L);
    }
}
