package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.exceptionhandling.ServiceNotFoundException;
import com.api.apicheck_incheck_out.repository.ServiceRepository;
import com.api.apicheck_incheck_out.service.ServicesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicesServiceImpl implements ServicesService {

    private final ServiceRepository serviceRepository;
    @Override
    public Services addService(Services service) {
        return serviceRepository.save(service);
    }

    @Override
    public Services updateService(Long id, Services updatedServices) {
        Services existedService = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service non trouvé avec l'id " + id));

        existedService.setNom(updatedServices.getNom());
        existedService.setDescription(updatedServices.getDescription());
        existedService.setPrix(updatedServices.getPrix());

        return serviceRepository.save(existedService);
    }

    @Override
    public Services deleteService(Long id) {
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service non trouvé avec l'id " + id));

        serviceRepository.deleteById(id);
        return service;
    }


    @Override
    public List<Services> getAllServices() {
        return serviceRepository.findAll();
    }
}
