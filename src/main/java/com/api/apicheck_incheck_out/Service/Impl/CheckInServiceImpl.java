package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.DocumentScanMock.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.DocumentScanMock.Entity.DocumentScan;
import com.api.apicheck_incheck_out.DocumentScanMock.Repository.DocumentScanRepository;
import com.api.apicheck_incheck_out.DocumentScanMock.Service.DocumentScanService;
import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Check_In;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.*;


import com.api.apicheck_incheck_out.Repository.ChambreRepository;
import com.api.apicheck_incheck_out.Repository.CheckInRepository;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Service.CheckInService;
import com.api.apicheck_incheck_out.Service.FactureService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CheckInServiceImpl implements CheckInService {
    private final CheckInRepository checkInRepository;
    private final DocumentScanRepository documentScanRepository;
    private final FactureService factureService;
    private final ReservationRepository reservationRepository;
    private final ChambreRepository chambreRepository;

    public CheckInServiceImpl(CheckInRepository checkInRepository, DocumentScanRepository documentScanRepository, FactureService factureService, ReservationRepository reservationRepository, ChambreRepository chambreRepository) {
        this.checkInRepository = checkInRepository;
        this.documentScanRepository = documentScanRepository;
        this.factureService = factureService;
        this.reservationRepository = reservationRepository;
        this.chambreRepository = chambreRepository;

    }

    @Override
    public Boolean validerScan(Reservation reservation, DocumentScanDTO doc) {
        //1:on verifier les données (comparaison)
        if(!reservation.getUser().getNom().equalsIgnoreCase(doc.getNom())||!reservation.getUser().getPrenom().equalsIgnoreCase(doc.getPrenom()))
        {
            throw new RuntimeException("Nom ou prénom incorrect !");
        }
        if(doc.getType()== DocumentScanType.CIN){
            if(reservation.getUser().getCin()==null|| !reservation.getUser().getCin().equalsIgnoreCase(doc.getCin())){
                throw new RuntimeException("CIN non valide !");
            }else if(doc.getType()== DocumentScanType.PASSPORT) {
                if (reservation.getUser().getNumeroPassport() == null || !reservation.getUser().getNumeroPassport().equalsIgnoreCase(doc.getPassport())) {
                    throw new RuntimeException("Passport non valide !");
                }
            }

        }
        //2: Apres la verification on enregistre le documentScan dans la bd
        DocumentScan documentScan=new DocumentScan();
        documentScan.setNom(doc.getNom());
        documentScan.setPrenom(doc.getPrenom());
        documentScan.setCin(doc.getCin());
        documentScan.setPassport(doc.getPassport());
        documentScan.setType(doc.getType());

        documentScanRepository.save(documentScan);

        //3: On cree checkin
        Check_In checkIn=new Check_In();
        checkIn.setDateCheckIn(LocalDate.now());
        checkIn.setReservation(reservation);
        checkIn.setDocumentScan(documentScan);
        checkIn.setStatus(CheckInStatus.En_Attente);
        documentScan.setCheckIn(checkIn);

        checkInRepository.save(checkIn);

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
    public Boolean validerCheckIn(Reservation reservation, PaiementMethod method) {
        Check_In checkIn = checkInRepository.findByReservation(reservation)
                .orElseThrow(() -> new RuntimeException("Aucun check-in trouvé pour cette réservation."));

        if(checkIn.getDocumentScan()==null){
            throw new RuntimeException("Le scan du document n'a pas été effectué.");
        }
        if(checkIn.getStatus()!=CheckInStatus.En_Attente){
            throw new RuntimeException("Le check-in n'est pas en attente.");

        }
        boolean paiement=factureService.payerFactureCheckIn(reservation,method);
        if(!paiement){
            throw new RuntimeException("Echec du paiement.");
        }

        checkIn.setStatus(CheckInStatus.Validé);
        checkInRepository.save(checkIn);

        reservation.setStatus(ReservationStatus.Confirmee);
        reservationRepository.save(reservation);

        List<Chambre> chambres = chambreRepository.findByReservation(reservation);


        for (Chambre chambre : chambres) {

            if (chambre.getReservation() != null && chambre.getReservation().getId().equals(reservation.getId())) {

                chambre.setStatut(ChambreStatut.OCCUPEE);
            }
        }


        chambreRepository.saveAll(chambres);


        return true;
    }
}
