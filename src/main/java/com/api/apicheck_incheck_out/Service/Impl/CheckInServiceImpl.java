package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.Entity.DocumentScan;
import com.api.apicheck_incheck_out.Mapper.DocumentScanMapper;
import com.api.apicheck_incheck_out.Repository.DocumentScanRepository;
import com.api.apicheck_incheck_out.Entity.*;
import com.api.apicheck_incheck_out.Enums.*;


import com.api.apicheck_incheck_out.Repository.*;
import com.api.apicheck_incheck_out.Service.CheckInService;
import com.api.apicheck_incheck_out.Service.FactureService;
import com.api.apicheck_incheck_out.Service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CheckInServiceImpl implements CheckInService {
    private final CheckInRepository checkInRepository;
    private final DocumentScanRepository documentScanRepository;
    private final FactureService factureService;
    private final NotificationService notificationService;
    private final ReservationRepository reservationRepository;
    private final ChambreRepository chambreRepository;
    private final FactureRepository factureRepository;
    private final NotificationRepository notificationRepository;
    private final ChambreReservationRepository chambreReservationRepository;
    private final DocumentScanMapper documentScanMapper;

    public CheckInServiceImpl(CheckInRepository checkInRepository, DocumentScanRepository documentScanRepository, FactureService factureService, NotificationService notificationService, ReservationRepository reservationRepository, ChambreRepository chambreRepository, FactureRepository factureRepository, NotificationRepository notificationRepository, ChambreReservationRepository chambreReservationRepository, DocumentScanMapper documentScanMapper) {
        this.checkInRepository = checkInRepository;
        this.documentScanRepository = documentScanRepository;
        this.factureService = factureService;
        this.notificationService = notificationService;
        this.reservationRepository = reservationRepository;
        this.chambreRepository = chambreRepository;
        this.factureRepository = factureRepository;
        this.notificationRepository = notificationRepository;
        this.chambreReservationRepository = chambreReservationRepository;
        this.documentScanMapper = documentScanMapper;
    }

    @Override
    public Boolean validerScan(Reservation reservation, DocumentScanDTO doc) {
        //1:on verifier les données (comparaison)
        if(!reservation.getUser().getNom().equalsIgnoreCase(doc.getNom())||!reservation.getUser().getPrenom().equalsIgnoreCase(doc.getPrenom()))
        {
            throw new RuntimeException("Nom ou prénom incorrect !");
        }
        if(doc.getType()== DocumentScanType.CIN) {
            if (reservation.getUser().getCin() == null || !reservation.getUser().getCin().equalsIgnoreCase(doc.getCin())) {
                throw new RuntimeException("CIN non valide !");
            }
        }else if(doc.getType()== DocumentScanType.PASSPORT) {
                if (reservation.getUser().getNumeroPassport() == null || !reservation.getUser().getNumeroPassport().equalsIgnoreCase(doc.getPassport())) {
                    throw new RuntimeException("Passport non valide !");
                }
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
        Check_In checkIn=new Check_In();
        checkIn.setDateCheckIn(LocalDate.now());
        checkIn.setReservation(reservation);
        checkIn.setDocumentScan(documentScan);
        checkIn.setStatus(CheckInStatus.En_Attente);
        documentScan.setCheckIn(checkIn);

        checkInRepository.save(checkIn);
        notificationService.notifier(reservation.getUser().getId(),"Votre check-in est en attente. Merci de procéder au paiement demandé.");

        return true;
    }

    @Override
    public DocumentScan getDocumentByCheckin(Long id) {
        Optional<Check_In> checkIn=checkInRepository.findById(id);
        if(checkIn.isPresent()){
            return checkIn.get().getDocumentScan();
        }
        throw new RuntimeException("Checkin introuvable avec l'id "+id);
    }

    @Override
    public Boolean validerCheckIn(Reservation reservation) {
        Check_In checkIn = checkInRepository.findByReservation(reservation)
                .orElseThrow(() -> new RuntimeException("Aucun check-in trouvé pour cette réservation."));

        if(checkIn.getDocumentScan()==null){
            throw new RuntimeException("Le scan du document n'a pas été effectué.");
        }
        if(checkIn.getStatus()!=CheckInStatus.En_Attente){
            throw new RuntimeException("Le check-in n'est pas en attente.");

        }


        factureService.payerFactureCheckIn(reservation);


        checkIn.setStatus(CheckInStatus.Validé);
        checkInRepository.save(checkIn);

        reservation.setStatus(ReservationStatus.Confirmee);
        reservationRepository.save(reservation);

        notificationService.notifier(reservation.getUser().getId(),"Réservation Confirmée ,Numéro de reservation :"+reservation.getId());

        List<ChambreReservation> chambreReservations = reservation.getChambreReservations();


        for (ChambreReservation chambreReservation : chambreReservations) {

            if (chambreReservation.getReservation().getId().equals(reservation.getId())) {

                chambreReservation.setStatut(ChambreStatut.OCCUPEE);
            }
        }


        chambreReservationRepository.saveAll(chambreReservations);


        return true;
    }
    @Override
    public Check_In getCheckInByReservation(Long Id_reservation) {

        Optional<Reservation> reservation = reservationRepository.findById(Id_reservation);
        if (reservation.isEmpty()) {
            throw new EntityNotFoundException("Réservation non trouvée avec l'id : " + Id_reservation);
        }

        return checkInRepository.findByReservation(reservation.get()).orElse(null);

    }


    @Override
    public CheckInStatus getStatusCheckIn(Long Id_reservation) {
        Reservation reservation = reservationRepository.findById(Id_reservation)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'id : " + Id_reservation));

        Check_In checkIn = checkInRepository.findByReservation(reservation)
                .orElseThrow(() -> new EntityNotFoundException("Aucun check-in trouvé pour la réservation avec l'id : " + Id_reservation));

        return checkIn.getStatus();
    }
    @Override
    public void validerCheckinReception(Long id_checkin){
        Check_In checkIn=checkInRepository.findById(id_checkin).orElseThrow(()->new RuntimeException("check_in non effectué!"));

        Reservation reservation = reservationRepository.findById(checkIn.getReservation().getId())
                .orElseThrow(() -> new RuntimeException("Reservation non trouvée"));

        factureService.payerFactureCheckInCache(reservation);

        checkIn.setStatus(CheckInStatus.Validé);

        checkInRepository.save(checkIn);

    }

    @Override
    public void ajoutercheckinReception(Long id_reservation,DocumentScan documentScan){
        Reservation reservation = reservationRepository.findById(id_reservation)
                .orElseThrow(() -> new RuntimeException("Reservation non trouvée"));

        documentScanRepository.save(documentScan);
        factureService.payerFactureCheckInCache(reservation);

        Check_In checkIn=new Check_In();
        checkIn.setReservation(reservation);
        checkIn.setDocumentScan(documentScan);
        checkIn.setDateCheckIn(LocalDate.now());
        checkIn.setStatus(CheckInStatus.Validé);

        checkInRepository.save(checkIn);

        reservation.setStatus(ReservationStatus.Confirmee);
        reservationRepository.save(reservation);

    }

}
