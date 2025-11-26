package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.exceptionhandling.ServiceNotFoundException;
import com.api.apicheck_incheck_out.repository.ServiceRepository;
import com.api.apicheck_incheck_out.service.ServicesService;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ServicesServiceImpl implements ServicesService {
    private final ServiceRepository serviceRepository;

    public ServicesServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public Services addService(Services service) {
        return serviceRepository.save(service);
    }

    @Override
    public Services updateService(Long id, Services updatedServices) {
        Optional<Services> service=serviceRepository.findById(id);
        if(service.isPresent()){
            Services existedService =service.get();
            existedService.setNom(updatedServices.getNom());
            existedService.setDescription(updatedServices.getDescription());
            existedService.setPrix(updatedServices.getPrix());
            return serviceRepository.save(existedService);
        }else{

            throw new ServiceNotFoundException("Service non trouvé avec l'id "+id);

        }
    }

    @Override
    public Services deleteService(Long idReservation) {
        Optional<Services> services=serviceRepository.findById(idReservation);
        if(services.isPresent()){
            serviceRepository.deleteById(idReservation);
            return services.get();
        }
        else{
            throw new ServiceNotFoundException("Service non trouvé avec l'id "+ idReservation);
        }
    }

    @Override
    public List<Services> getAllServices() {
        return serviceRepository.findAll();
    }
}
