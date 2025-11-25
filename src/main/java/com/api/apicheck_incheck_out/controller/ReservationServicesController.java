package com.api.apicheck_incheck_out.controller;

import com.api.apicheck_incheck_out.dto.ReservationServiceRequestDTO;
import com.api.apicheck_incheck_out.dto.ReservationServicesDTO;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.mapper.ReservationServicesMapper;
import com.api.apicheck_incheck_out.repository.NotificationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.impl.EmailSenderService;
import com.api.apicheck_incheck_out.service.NotificationService;
import com.api.apicheck_incheck_out.service.ReservationServicesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


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

    @GetMapping("by-reservation/{idReservation}/user/{idUser}")
    public ResponseEntity<List<ReservationServiceRequestDTO>> getServicesByReservation(@PathVariable Long idReservation, @PathVariable Long idUser) {

        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'id : " + idReservation));

        if (reservation == null || !reservation.getUser().getId().equals(idUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'avez pas accès à cette réservation !");
        }

        List<ReservationServices> reservationServices = reservationServicesService.getAllServicesByReservation(idReservation);


        List<ReservationServiceRequestDTO> dtoList = reservationServices.stream()
                .map(reservationService -> {

                    Services service = reservationService.getService();


                    return new ReservationServiceRequestDTO(
                            reservation.getId(),
                            service.getId(),
                            service.getNom(),
                            service.getDescription(),
                            service.getPrix(),
                            reservationService.getPaiementStatus()
                    );
                })
                .toList();

        return ResponseEntity.ok(dtoList);
    }
    @PostMapping("/addSejourService")
    public ResponseEntity<String> addSejourServices(
            @RequestParam Long idReservation,
            @RequestBody List<Long> serviceIds) {
        reservationServicesService.addSejourServicesToReservation(idReservation, serviceIds);
        return ResponseEntity.ok("Services ajoutés avec succès !");
    }

    @GetMapping("by-reservation/{idReservation}/phase")
    public ResponseEntity<List<ReservationServicesDTO>> getServicesByPhase(
            @PathVariable Long idReservation,
            @RequestParam PhaseAjoutService phase) {
        List<ReservationServices> reservationServicesList=reservationServicesService.getServicesByPhase(idReservation,phase);
        List<ReservationServicesDTO> dtoList=reservationServicesList.stream()
                .map(reservationServicesMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/addService")
    public ResponseEntity<String> addReservationServices(
            @RequestParam Long idReservation,
            @RequestBody List<Long> serviceIds) {

        Reservation reservation=reservationRepository.findById(idReservation).orElseThrow(
                ()->new RuntimeException("Réservation non trouvée avec l'id :"+idReservation)
        );



        if(!serviceIds.isEmpty()){
            reservationServicesService.addResService(idReservation, serviceIds);

        }

        List<String> chambresNames = reservation.getChambreReservations().stream()
                .map(chambreReservation -> chambreReservation.getChambre().getNom())
                .toList();

        List<String> servicesNames = reservation.getServiceList().stream()
                .map(serviceReservation -> serviceReservation.getService().getNom())
                .toList();


        String chambresList = String.join(", ", chambresNames);
        String servicesList=String.join(" , ",servicesNames);

        emailSenderService.sendEmail(reservation.getUser().getEmail(),
                "Réservation",
                "Vous avez une réservation En attente de check-in avec les informations suivantes:"+"\n"+
                "Numéro réservation :"+ reservation.getId()+"\n"+
                "Date de debut :"+ reservation.getDateDebut()+"\n"+
                "Date de fin :"+reservation.getDateFin()+"\n"+
                "List des chambres réservées :"+chambresList+"\n"+
                "List des services choisis :"+servicesList
        );
        Notification notif= notificationService.notifier(reservation.getUser().getId(),"Vous avez une réservation En attente de check-in ,Numéro réservation :"+ reservation.getId());
        notificationRepository.save(notif);
        return ResponseEntity.ok("Services traités et email envoyé.");
    }
    @GetMapping("/available-services/{idReservation}")
    public ResponseEntity<List<Services>> getAvailableServices(@PathVariable Long idReservation) {
        List<Services> availableServices = reservationServicesService.getAvailableServices(idReservation);
        return ResponseEntity.ok(availableServices);
    }
    @GetMapping("/during-stay/{idReservationService}")
    public ResponseEntity<List<Services>> findUnpaidServicesDuringStay(@PathVariable("idReservationService") Long idReservationService){
        try{
            return ResponseEntity.ok(reservationServicesService.getRsrvServicesSejourUnpaid(idReservationService));
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }
}
