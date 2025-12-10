package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.CheckOut;
import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.exceptionhandling.CheckOutNotFoundException;
import com.api.apicheck_incheck_out.pdf.CheckoutFacturePDF;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.service.ReservationServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ServicesPaymentManager {

    ReservationServicesService reservationServicesService;
    CheckOutFinder checkOutFinder;
    ReservationServiceRepository reservationServiceRepository;

    public double totalPrice(Long id){
        CheckOut checkout = checkOutFinder.findById(id);
        Long idReservation = checkout.getReservation().getId();
        return reservationServicesService.getAllServicesByReservation(idReservation).stream()
                .mapToDouble(rsrvservice -> rsrvservice.getService().getPrix())
                .sum();
    }

    public void handleServicesPayment(Long idrsrv){
        List<ReservationServices> rsrvServices = reservationServicesService.getServicesByPhase(idrsrv, PhaseAjoutService.SEJOUR);
        List<Services> services = rsrvServices.stream()
                .map(rs -> rs.getService())
                .filter(Objects::nonNull)
                .toList();
        CheckoutFacturePDF.gerercheckOutFacturePDF(checkOutFinder.findByReservation(idrsrv).getReservation(),services,totalPrice(checkOutFinder.findByReservation(idrsrv).getId()));
        rsrvServices.stream().forEach(service ->
                service.setPaiementStatus(PaiementStatus.PAYE)
        );
        reservationServiceRepository.saveAll(rsrvServices);

    }
}
