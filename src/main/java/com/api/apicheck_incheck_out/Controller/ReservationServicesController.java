package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.ReservationServiceRequestDTO;
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
import com.api.apicheck_incheck_out.Service.ReservationService;
import com.api.apicheck_incheck_out.Service.ReservationServicesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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




    public ReservationServicesController(ReservationServicesService reservationServicesService, ReservationServicesMapper reservationServicesMapper, ReservationRepository reservationRepository, EmailSenderService emailSenderService, NotificationService notificationService, NotificationRepository notificationRepository, ReservationService reservationService, ReservationService reservationServ) {
        this.reservationServicesService = reservationServicesService;
        this.reservationServicesMapper = reservationServicesMapper;
        this.reservationRepository = reservationRepository;
        this.emailSenderService = emailSenderService;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("by-reservation/{id_reservation}/user/{id_user}")
    public ResponseEntity<List<ReservationServiceRequestDTO>> getServicesByReservation(@PathVariable Long id_reservation, @PathVariable Long id_user) {

        Reservation reservation = reservationRepository.findById(id_reservation)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'id : " + id_reservation));

        if (reservation == null || !reservation.getUser().getId().equals(id_user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'avez pas accès à cette réservation !");
        }

        List<ReservationServices> reservationServices = reservationServicesService.getAllServicesByReservation(id_reservation);


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
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }
    @PostMapping("/addSejourService")
    public ResponseEntity<?> addSejourServices(
            @RequestParam Long id_reservation,
            @RequestBody List<Long> serviceIds) {
        reservationServicesService.addSejourServicesToReservation(id_reservation, serviceIds);
        return ResponseEntity.ok("Services ajoutés avec succès !");
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
//        List<String> servicesNames =new ArrayList<>();

        if(!serviceIds.isEmpty()){
            reservationServices=reservationServicesService.addResService(id_reservation, serviceIds);

        }

        List<String> chambresNames = reservation.getChambreReservations().stream()
                .map(chambreReservation -> chambreReservation.getChambre().getNom())
                .collect(Collectors.toList());

        List<String> servicesNames = reservation.getServiceList().stream()
                .map(serviceReservation -> serviceReservation.getService().getNom())
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
    @GetMapping("/available-services/{id_reservation}")
    public ResponseEntity<List<Services>> getAvailableServices(@PathVariable Long id_reservation) {
        List<Services> availableServices = reservationServicesService.getAvailableServices(id_reservation);
        return ResponseEntity.ok(availableServices);
    }
    @GetMapping("/during-stay/{id_rsrv}")
    public ResponseEntity<List<Services>> findUnpaidServicesDuringStay(@PathVariable("id_rsrv") Long id_rsrvr){
        try{
            return ResponseEntity.ok(reservationServicesService.getRsrvServicesSejourUnpaid(id_rsrvr));
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }
}
