package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.exceptionhandling.*;
import com.api.apicheck_incheck_out.repository.DocumentScanRepository;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.*;


import com.api.apicheck_incheck_out.repository.*;
import com.api.apicheck_incheck_out.service.CheckInService;
import com.api.apicheck_incheck_out.service.FactureService;
import com.api.apicheck_incheck_out.service.NotificationService;
import com.api.apicheck_incheck_out.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class CheckInServiceImpl implements CheckInService {
    private final CheckInRepository checkInRepository;
    private final DocumentScanRepository documentScanRepository;
    private final FactureService factureService;
    private final NotificationService notificationService;
    private final ReservationRepository reservationRepository;
    private final ChambreReservationRepository chambreReservationRepository;
    private final ReservationServiceRepository reservationServiceRepository;

    private final UserService userService;

    public static final String CHECKINVALID="Check-In validé pour la réservation numéro : ";

    public CheckInServiceImpl(CheckInRepository checkInRepository, DocumentScanRepository documentScanRepository, FactureService factureService, NotificationService notificationService, ReservationRepository reservationRepository,ChambreReservationRepository chambreReservationRepository,ReservationServiceRepository reservationServiceRepository, UserService userService) {
        this.checkInRepository = checkInRepository;
        this.documentScanRepository = documentScanRepository;
        this.factureService = factureService;
        this.notificationService = notificationService;
        this.reservationRepository = reservationRepository;
        this.chambreReservationRepository = chambreReservationRepository;
        this.reservationServiceRepository = reservationServiceRepository;
        this.userService = userService;
    }

    @Override
    public Boolean validerScan(Reservation reservation, DocumentScanDTO doc) {
        //1:on verifier les données (comparaison)
        if(!reservation.getUser().getNom().equalsIgnoreCase(doc.getNom())||!reservation.getUser().getPrenom().equalsIgnoreCase(doc.getPrenom()))
        {
            throw new InvalidNameException("Nom ou prénom incorrect !");
        }
        if(doc.getType()== DocumentScanType.CIN && (reservation.getUser().getCin() == null || !reservation.getUser().getCin().equalsIgnoreCase(doc.getCin()))) {

                throw new InvalidCINException("CIN non valide !");

        }else if(doc.getType()== DocumentScanType.PASSPORT && (reservation.getUser().getNumeroPassport() == null || !reservation.getUser().getNumeroPassport().equalsIgnoreCase(doc.getPassport()))) {

                    throw new InvalidPassportException("Passport non valide !");

            }


        //2: Apres la verification on enregistre le documentScan dans la bd
        DocumentScan documentScan=new DocumentScan();
        documentScan.setNom(doc.getNom());
        documentScan.setPrenom(doc.getPrenom());
        documentScan.setCin(doc.getCin());
        documentScan.setPassport(doc.getPassport());
        documentScan.setType(doc.getType());
        documentScan.setImage(doc.getImage());
        documentScan.setFileName(doc.getFileName());
        documentScan.setFileType(doc.getFileType());

        documentScanRepository.save(documentScan);

        //3: On cree checkin
        CheckIn checkIn=new CheckIn();
        checkIn.setDateCheckIn(LocalDate.now());
        checkIn.setReservation(reservation);
        checkIn.setDocumentScan(documentScan);
        checkIn.setStatus(CheckInStatus.EN_ATTENTE);
        documentScan.setCheckIn(checkIn);

        checkInRepository.save(checkIn);
        notificationService.notifier(reservation.getUser().getId(),"Votre check-in est en attente. Merci de procéder au paiement demandé.");

        return true;
    }

    @Override
    public DocumentScan getDocumentByCheckin(Long id) {
        Optional<CheckIn> checkIn=checkInRepository.findById(id);
        if(checkIn.isPresent()){
            return checkIn.get().getDocumentScan();
        }
        throw new CheckInNotFoundException("Checkin introuvable avec l'id "+id);
    }

    @Override
    public Boolean validerCheckIn(Reservation reservation) {
        CheckIn checkIn = checkInRepository.findByReservation(reservation)
                .orElseThrow(() -> new CheckInNotFoundException("Aucun check-in trouvé pour cette réservation."));

        if(checkIn.getDocumentScan()==null){
            throw new DocumentNotScannedException("Le scan du document n'a pas été effectué.");
        }
        if(checkIn.getStatus()!=CheckInStatus.EN_ATTENTE){
            throw new InvalidCheckInStatusException("Le check-in n'est pas en attente.");

        }


        factureService.payerFactureCheckIn(reservation);


        checkIn.setStatus(CheckInStatus.VALIDE);
        checkInRepository.save(checkIn);

        reservation.setStatus(ReservationStatus.CONFIRMEE);
        reservationRepository.save(reservation);

        notificationService.notifier(reservation.getUser().getId(),"Réservation Confirmée ,Numéro de reservation :"+reservation.getId());

        List<ChambreReservation> chambreReservations = reservation.getChambreReservations();


        for (ChambreReservation chambreReservation : chambreReservations) {

            if (chambreReservation.getReservation().getId().equals(reservation.getId())) {

                chambreReservation.setStatut(ChambreStatut.OCCUPEE);
            }
        }

        chambreReservationRepository.saveAll(chambreReservations);

        List<ReservationServices> reservationServices=reservation.getServiceList();

        for(ReservationServices service:reservationServices){
            if(service.getReservation().getId().equals(reservation.getId())){
                service.setPaiementStatus(PaiementStatus.PAYE);
            }
        }
        List<User> admins = userService.getAdmins();
        admins.stream().forEach(admin ->
            notificationService.notifier(admin.getId(),CHECKINVALID+ reservation.getId())
        );
        List<UserDto> receps = userService.getReceptionists();
        receps.stream().forEach(recep ->
            notificationService.notifier(recep.getId(),CHECKINVALID+ reservation.getId())
        );
        reservationServiceRepository.saveAll(reservationServices);

        return true;
    }
    @Override
    public List<CheckIn> checkinsForToday(LocalDate today){
        List<Reservation> reservations = reservationRepository.findByDateDebut(today);
        return reservations.stream()
                .map(Reservation::getCheckIn)
                .filter(Objects::nonNull)
                .toList();
    }
    @Override
    public CheckIn getCheckInByReservation(Long idReservation) {

        Optional<Reservation> reservation = reservationRepository.findById(idReservation);
        if (reservation.isEmpty()) {
            throw new EntityNotFoundException("Réservation non trouvée avec l'id : " + idReservation);
        }

        return checkInRepository.findByReservation(reservation.get()).orElse(null);

    }


    @Override
    public CheckInStatus getStatusCheckIn(Long idReservation) {
        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new ReservationNotFoundException("Réservation non trouvée avec l'id : " + idReservation));

        CheckIn checkIn = checkInRepository.findByReservation(reservation)
                .orElseThrow(() -> new CheckInNotFoundException("Aucun check-in trouvé pour la réservation avec l'id : " + idReservation));

        return checkIn.getStatus();
    }
    @Override
    public void validerCheckinReception(Long idCheckin){
        CheckIn checkIn=checkInRepository.findById(idCheckin).orElseThrow(()->new CheckInNotFoundException("check_in non effectué!"));

        Reservation reservation = reservationRepository.findById(checkIn.getReservation().getId())
                .orElseThrow(() -> new ReservationNotFoundException("Reservation non trouvée"));

        factureService.payerFactureCheckInCache(reservation);

        checkIn.setStatus(CheckInStatus.VALIDE);

        checkInRepository.save(checkIn);
        notificationService.notifier(reservation.getUser().getId(),"Réservation Confirmée ,Numéro de reservation :"+reservation.getId());
        List<User> admins = userService.getAdmins();
        admins.stream().forEach(admin ->
            notificationService.notifier(admin.getId(),CHECKINVALID+ reservation.getId())

        );
        List<UserDto> receps = userService.getReceptionists();
        receps.stream().forEach(recep ->
            notificationService.notifier(recep.getId(),CHECKINVALID+ reservation.getId())
        );

    }

    @Override
    public void ajoutercheckinReception(Long idReservation,DocumentScan documentScan){
        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation non trouvée"));

        documentScanRepository.save(documentScan);
        factureService.payerFactureCheckInCache(reservation);

        CheckIn checkIn=new CheckIn();
        checkIn.setReservation(reservation);
        checkIn.setDocumentScan(documentScan);
        checkIn.setDateCheckIn(LocalDate.now());
        checkIn.setStatus(CheckInStatus.VALIDE);

        checkInRepository.save(checkIn);

        reservation.setStatus(ReservationStatus.CONFIRMEE);

        reservationRepository.save(reservation);
        List<User> admins = userService.getAdmins();
        admins.stream().forEach(admin ->
            notificationService.notifier(admin.getId(),"Check-In ajouté pour la réservation numéro : "+ reservation.getId())

        );
        List<UserDto> receps = userService.getReceptionists();
        receps.stream().forEach(recep ->
            notificationService.notifier(recep.getId(),"Check-In ajouté pour la réservation numéro : "+ reservation.getId())
        );

    }

}
