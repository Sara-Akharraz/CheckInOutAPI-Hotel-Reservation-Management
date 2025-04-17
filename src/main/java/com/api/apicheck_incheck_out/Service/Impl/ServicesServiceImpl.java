package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Entity.Services;
import com.api.apicheck_incheck_out.Repository.ServiceRepository;
import com.api.apicheck_incheck_out.Service.ServicesService;
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
            throw new RuntimeException("Service non trouvé avec L'id:" +id);
        }
    }

    @Override
    public Services deleteService(Long id_service) {
        Optional<Services> services=serviceRepository.findById(id_service);
        if(services.isPresent()){
            serviceRepository.deleteById(id_service);
            return services.get();
        }
        else{
            throw new RuntimeException("Service non trouvé avec l'id "+id_service);
        }
    }

    @Override
    public List<Services> getAllServices() {
        return serviceRepository.findAll();
    }
}
