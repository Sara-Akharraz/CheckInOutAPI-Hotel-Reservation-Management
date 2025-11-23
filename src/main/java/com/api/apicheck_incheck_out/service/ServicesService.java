package com.api.apicheck_incheck_out.service;


import com.api.apicheck_incheck_out.entity.Services;


import java.util.List;

public interface ServicesService {
    //pour le post Admin
    public Services addService(Services service);
    public Services updateService(Long id,Services updatedService);
    public Services deleteService(Long idReservation);
    public List<Services> getAllServices();

}
