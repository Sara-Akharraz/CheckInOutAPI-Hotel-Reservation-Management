package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.ReservationServicesDTO;
import com.api.apicheck_incheck_out.Entity.*;
import com.api.apicheck_incheck_out.Enums.PaiementStatus;
import com.api.apicheck_incheck_out.Enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.Mapper.ReservationServicesMapper;
import com.api.apicheck_incheck_out.Repository.NotificationRepository;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Repository.ServiceRepository;
import com.api.apicheck_incheck_out.Service.Impl.EmailSenderService;
import com.api.apicheck_incheck_out.Service.NotificationService;
import com.api.apicheck_incheck_out.Service.ReservationServicesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservation-services")
public class ReservationServicesController {
    private final ReservationServicesService reservationServicesService;
    private final ReservationServicesMapper reservationServicesMapper;
    private final ReservationRepository reservationRepository;
    private final EmailSenderService emailSenderService;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;


    public ReservationServicesController(ReservationServicesService reservationServicesService, ReservationServicesMapper reservationServicesMapper, ReservationRepository reservationRepository, EmailSenderService emailSenderService, NotificationService notificationService, NotificationRepository notificationRepository) {
        this.reservationServicesService = reservationServicesService;
        this.reservationServicesMapper = reservationServicesMapper;
        this.reservationRepository = reservationRepository;
        this.emailSenderService = emailSenderService;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
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

    @PostMapping("/addService")
    public ResponseEntity<?> addReservationServices(
            @RequestParam Long id_reservation,
            @RequestBody List<Long> serviceIds) {

        Reservation reservation=reservationRepository.findById(id_reservation).orElseThrow(
                ()->new RuntimeException("Réservation non trouvée avec l'id :"+id_reservation)
        );
        List<ReservationServices> reservationServices=new ArrayList<>();
        List<String> servicesNames =new ArrayList<>();

        if(!serviceIds.isEmpty()){
            reservationServices=reservationServicesService.addResService(id_reservation, serviceIds);
            servicesNames=reservationServices.stream()
                    .map(rs -> rs.getService().getNom())
                    .collect(Collectors.toList());
        }

        List<String> chambresNames = reservation.getChambreReservations().stream()
                .map(chambreReservation -> chambreReservation.getChambre().getNom())
                .collect(Collectors.toList());

        String chambresList = String.join(", ", chambresNames);
        String servicesList=String.join(" , ",servicesNames);

        emailSenderService.sendEmail(reservation.getUser().getEmail(),
                "Réservation",
                "Vous avez une réservation En attente de check-in avec les informations suivantes:"+"\n"+
                "Numéro réservation :"+ reservation.getId()+"\n"+
                "Date de debut :"+ reservation.getDate_debut()+"\n"+
                "Date de fin :"+reservation.getDate_fin()+"\n"+
                "List des chambres réservées :"+chambresList+"\n"+
                "List des services choisis :"+servicesList
        );
        Notification notif= notificationService.notifier(reservation.getUser().getId(),"Vous avez une réservation En attente de check-in ,Numéro réservation :"+ reservation.getId());
        notificationRepository.save(notif);
        return ResponseEntity.ok("Services traités et email envoyé.");
    }
}
