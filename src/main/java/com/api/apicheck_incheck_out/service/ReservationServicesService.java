package com.api.apicheck_incheck_out.service;

import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;

import java.util.List;

public interface ReservationServicesService {
    public List<ReservationServices> getAllServicesByReservation(Long idReservation);
    public List<ReservationServices> getServicesByPhase(Long idReservation, PhaseAjoutService phase);
    public void addSejourServicesToReservation(Long idReservation,List<Long> serviceIds);
    public List<ReservationServices> addResService(Long idReservation,List<Long> serviceIds);
    public List<Services> getAvailableServices(Long idReservation);
    public List<Services> getRsrvServicesSejourUnpaid(Long id);
}
