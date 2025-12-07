package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.exceptionhandling.*;
import com.api.apicheck_incheck_out.repository.DocumentScanRepository;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.*;

import com.api.apicheck_incheck_out.service.CheckInService;
import com.api.apicheck_incheck_out.service.FactureService;
import com.api.apicheck_incheck_out.service.factory.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;



@Service
public class CheckInServiceImpl implements CheckInService {
    private final CheckInFinder checkInFinder;
    private final DocumentScanValidator documentScanValidator;
    private final DocumentScanFactory documentScanFactory;
    private final CheckInFactory checkInFactory;
    private final CheckInValidator checkInValidator;
    private final CheckInStatusManager checkInStatusManager;
    private final CheckInNotificationManager notificationManager;
    private final ReservationConfirmationManager reservationConfirmationManager;
    private final ReservationServicesFinder reservationFinder;
    private final FactureService factureService;
    private final DocumentScanRepository documentScanRepository;

    public CheckInServiceImpl(
            CheckInFinder checkInFinder,
            DocumentScanValidator documentScanValidator,
            DocumentScanFactory documentScanFactory,
            CheckInFactory checkInFactory,
            CheckInValidator checkInValidator,
            CheckInStatusManager checkInStatusManager,
            CheckInNotificationManager notificationManager,
            ReservationConfirmationManager reservationConfirmationManager,
            ReservationServicesFinder reservationFinder,
            FactureService factureService,
            DocumentScanRepository documentScanRepository) {
        this.checkInFinder = checkInFinder;
        this.documentScanValidator = documentScanValidator;
        this.documentScanFactory = documentScanFactory;
        this.checkInFactory = checkInFactory;
        this.checkInValidator = checkInValidator;
        this.checkInStatusManager = checkInStatusManager;
        this.notificationManager = notificationManager;
        this.reservationConfirmationManager = reservationConfirmationManager;
        this.reservationFinder = reservationFinder;
        this.factureService = factureService;
        this.documentScanRepository = documentScanRepository;
    }

    @Override
    public Boolean validerScan(Reservation reservation, DocumentScanDTO doc) {
        documentScanValidator.validateDocument(reservation, doc);

        DocumentScan documentScan = documentScanFactory.createAndSave(doc);
        checkInFactory.createCheckIn(reservation, documentScan, CheckInStatus.EN_ATTENTE);

        notificationManager.notifyUserCheckInPending(reservation.getUser().getId());

        return true;
    }

    @Override
    public DocumentScan getDocumentByCheckin(Long id) {
        CheckIn checkIn = checkInFinder.findById(id);
        return checkIn.getDocumentScan();
    }

    @Override
    public Boolean validerCheckIn(Reservation reservation) {
        CheckIn checkIn = checkInFinder.findByReservation(reservation);
        checkInValidator.validateForConfirmation(checkIn);

        factureService.payerFactureCheckIn(reservation);

        checkInStatusManager.updateStatus(checkIn, CheckInStatus.VALIDE);
        reservationConfirmationManager.confirmReservation(reservation);

        notificationManager.notifyUserReservationConfirmed(
                reservation.getUser().getId(),
                reservation.getId()
        );
        notificationManager.notifyStaffCheckInValidated(reservation.getId());

        return true;
    }

    @Override
    public List<CheckIn> checkinsForToday(LocalDate today) {
        return checkInFinder.findCheckinsForToday(today);
    }

    @Override
    public CheckIn getCheckInByReservation(Long idReservation) {
        return checkInFinder.findByReservationId(idReservation);
    }

    @Override
    public CheckInStatus getStatusCheckIn(Long idReservation) {
        Reservation reservation = reservationFinder.findReservationById(idReservation);
        CheckIn checkIn = checkInFinder.findByReservation(reservation);
        return checkIn.getStatus();
    }

    @Override
    public void validerCheckinReception(Long idCheckin) {
        CheckIn checkIn = checkInFinder.findById(idCheckin);
        Reservation reservation = reservationFinder.findReservationById(
                checkIn.getReservation().getId()
        );

        factureService.payerFactureCheckInCache(reservation);
        checkInStatusManager.updateStatus(checkIn, CheckInStatus.VALIDE);

        notificationManager.notifyUserReservationConfirmed(
                reservation.getUser().getId(),
                reservation.getId()
        );
        notificationManager.notifyStaffCheckInValidated(reservation.getId());
    }

    @Override
    public void ajoutercheckinReception(Long idReservation, DocumentScan documentScan) {
        Reservation reservation = reservationFinder.findReservationById(idReservation);

        documentScanRepository.save(documentScan);
        factureService.payerFactureCheckInCache(reservation);

        checkInFactory.createCheckIn(reservation, documentScan, CheckInStatus.VALIDE);
        reservationConfirmationManager.confirmReservation(reservation);

        notificationManager.notifyStaffCheckInAdded(reservation.getId());
    }

}
