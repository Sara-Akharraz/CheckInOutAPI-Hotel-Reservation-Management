package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.ReservationServicesDTO;
import com.api.apicheck_incheck_out.Entity.ReservationServices;
import com.api.apicheck_incheck_out.Enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.Mapper.ReservationServicesMapper;
import com.api.apicheck_incheck_out.Service.ReservationServicesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservation-services")
public class ReservationServicesController {
    private final ReservationServicesService reservationServicesService;
    private final ReservationServicesMapper reservationServicesMapper;

    public ReservationServicesController(ReservationServicesService reservationServicesService, ReservationServicesMapper reservationServicesMapper) {
        this.reservationServicesService = reservationServicesService;
        this.reservationServicesMapper = reservationServicesMapper;
    }

    @GetMapping("by-reservation/{id_reservation}")

    public ResponseEntity<List<ReservationServicesDTO>> getServicesByReservation(@PathVariable Long id_reservation){
        List<ReservationServices> reservationServices=reservationServicesService.getAllServicesByReservation(id_reservation);

        List<ReservationServicesDTO> dtoList=reservationServices.stream()
                .map(reservationServicesMapper::toDTO)
                .toList();

        return ResponseEntity.ok(dtoList);

    }

    @GetMapping("by-reservation/{id_reservation}/phase")
    public ResponseEntity<List<ReservationServicesDTO>> getServicesByPhase(
            @PathVariable Long id_reservation,
            @RequestParam PhaseAjoutService phase) {
        List<ReservationServices> reservationServicesList=reservationServicesService.getServicesByPhase(id_reservation,phase);
        List<ReservationServicesDTO> dtoList=reservationServicesList.stream()
                .map(reservationServicesMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

}
