package com.api.apicheck_incheck_out.controllertest;


import com.api.apicheck_incheck_out.controller.ServiceController;
import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.security.JwtService;
import com.api.apicheck_incheck_out.service.ServicesService;
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
@WebMvcTest(ServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ServiceControllerTest {

    @MockBean
    ServicesService servicesService;
    @Autowired
    MockMvc mockMvc;
    Services service, service1;
    @MockBean
    private JwtService jwtService;
    @Autowired
    private ObjectMapper objectMapper;
    List<Services> servicesList = new ArrayList<>();
    @BeforeEach
    void setUp(){
        service = Services.builder()
                .id(1L)
                .nom("Internet")
                .prix(100)
                .description("Fibre optique")
                .build();
        service1 = Services.builder()
                .id(2L)
                .nom("Sport")
                .prix(200)
                .description("Salle avec Matériels")
                .build();
    }
    @Test
    void addServiceTest() throws Exception{
        when(servicesService.addService(any(Services.class))).thenReturn(service);

        mockMvc.perform(post("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(service)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(service.getId()))
                .andExpect(jsonPath("$.nom").value(service.getNom()));

        verify(servicesService, times(1)).addService(any(Services.class));
    }

    @Test
    void getAllServicesTest() throws Exception{

        servicesList.add(service);
        servicesList.add(service1);

        when(servicesService.getAllServices()).thenReturn(servicesList);

        mockMvc.perform(get("/api/services")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(service.getId()))
                .andExpect(jsonPath("$[0].nom").value(service.getNom()))
                .andExpect(jsonPath("$[1].id").value(service1.getId()))
                .andExpect(jsonPath("$[1].nom").value(service1.getNom()));

        verify(servicesService, times(1)).getAllServices();
    }

    @Test
    void updateServiceTest() throws Exception{

        service1.setId(1L);
        when(servicesService.updateService(service.getId(),service1)).thenReturn(service1);

        mockMvc.perform(put("/api/services/{id}",service.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(service1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(service1.getId()))
                .andExpect(jsonPath("$.nom").value(service1.getNom()));

        verify(servicesService, times(1)).updateService(service.getId(),service1);
    }

    @Test
    void deleteServiceTest() throws Exception{
        service1.setId(1L);
        when(servicesService.deleteService(service.getId())).thenReturn(service);

        mockMvc.perform(delete("/api/services/{id}",service.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(service)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(service.getId()))
                .andExpect(jsonPath("$.nom").value(service.getNom()));

        verify(servicesService, times(1)).deleteService(service.getId());
    }

}
