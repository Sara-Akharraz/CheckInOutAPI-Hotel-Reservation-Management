package com.api.apicheck_incheck_out.Service;


import com.api.apicheck_incheck_out.Entity.Services;


import java.util.List;

public interface ServicesService {
    //pour le post Admin
    public Services addService(Services service);
    public Services updateService(Long id,Services updatedService);
    public Services deleteService(Long id_service);
    public List<Services> getAllServices();

}