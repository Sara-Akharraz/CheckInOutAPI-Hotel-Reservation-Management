package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.entity.Services;

import com.api.apicheck_incheck_out.exceptionhandling.ServiceNotFoundException;
import com.api.apicheck_incheck_out.repository.ServiceRepository;
import com.api.apicheck_incheck_out.service.impl.ServicesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicesImplTest {
    @Mock
    private ServiceRepository serviceRepository;
    @InjectMocks
    private ServicesServiceImpl servicesService;
    private Services service;

    @BeforeEach
    void setup(){
        service=new Services();
        service.setId(1L);
        service.setNom("WiFi");
        service.setDescription("Internet haut débit");
        service.setPrix(45.0);
    }
    @Test
    void TestAddService(){
        when(serviceRepository.save(service)).thenReturn(service);

        Services result=servicesService.addService(service);

        assertEquals(service,result);
        verify(serviceRepository,times(1)).save(service);
    }
    @Test
    void TestupdateService(){
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(serviceRepository.save(any())).thenReturn(service);

        Services updated=new Services();
        updated.setNom("WiFi");
        updated.setDescription("Internet ultra rapide");
        updated.setPrix(55.0);

        Services result=servicesService.updateService(1L,updated);

        assertEquals("Internet ultra rapide",result.getDescription());
        assertEquals(55.0,result.getPrix());

        verify(serviceRepository,times(1)).save(any());
    }
    @Test
    void TestupdateServiceThrowsException(){
        Services updated=new Services();
        updated.setNom("WiFi");
        updated.setDescription("Internet ultra rapide");
        updated.setPrix(55.0);

        when(serviceRepository.findById(2L)).thenReturn(Optional.empty());
        ServiceNotFoundException ex=assertThrows(ServiceNotFoundException.class,()->servicesService.updateService(2L,updated));
        assertEquals("Service non trouvé avec l'id 2",ex.getMessage());

        verify(serviceRepository,times(1)).findById(2L);
        verify(serviceRepository,never()).save(any(Services.class));
    }
    @Test
    void TestdeleteService(){
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        doNothing().when(serviceRepository).deleteById(1L);

        Services result=servicesService.deleteService(1L);

        assertEquals(service,result);
        verify(serviceRepository,times(1)).deleteById(1L);
    }
    @Test
    void TestdeleteServiceThrowsException(){
        when(serviceRepository.findById(2L)).thenReturn(Optional.empty());

        ServiceNotFoundException ex=assertThrows(ServiceNotFoundException.class,()->servicesService.deleteService(2L));

        assertEquals("Service non trouvé avec l'id 2",ex.getMessage());
        verify(serviceRepository,times(1)).findById(2L);
        verify(serviceRepository,never()).deleteById(2L);
    }
    @Test
    void TestgetAllServices(){
        List<Services> servicesList= Arrays.asList(service);

        when(serviceRepository.findAll()).thenReturn(servicesList);

        List<Services> result=servicesService.getAllServices();

        assertEquals(1,result.size());
        assertEquals("WiFi",result.get(0).getNom());
        verify(serviceRepository,times(1)).findAll();
    }
}
