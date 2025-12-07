package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.CheckInStatus;
import com.api.apicheck_incheck_out.enums.DocumentScanType;
import com.api.apicheck_incheck_out.exceptionhandling.*;
import com.api.apicheck_incheck_out.repository.DocumentScanRepository;
import com.api.apicheck_incheck_out.service.FactureService;
import com.api.apicheck_incheck_out.service.factory.*;
import com.api.apicheck_incheck_out.service.impl.CheckInServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckInServiceImplTest {

    @Mock
    private CheckInFinder checkInFinder;

    @Mock
    private DocumentScanValidator documentScanValidator;

    @Mock
    private DocumentScanFactory documentScanFactory;

    @Mock
    private CheckInFactory checkInFactory;

    @Mock
    private CheckInValidator checkInValidator;

    @Mock
    private CheckInStatusManager checkInStatusManager;

    @Mock
    private CheckInNotificationManager notificationManager;

    @Mock
    private ReservationConfirmationManager reservationConfirmationManager;

    @Mock
    private ReservationServicesFinder reservationFinder;

    @Mock
    private FactureService factureService;

    @Mock
    private DocumentScanRepository documentScanRepository;

    @InjectMocks
    private CheckInServiceImpl checkInService;

    private Reservation reservation;
    private User user;
    private DocumentScanDTO documentScanDTO;
    private DocumentScan documentScan;
    private CheckIn checkIn;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setNom("Akharraz");
        user.setPrenom("Sara");
        user.setCin("AB123456");

        reservation = new Reservation();
        reservation.setId(100L);
        reservation.setUser(user);

        documentScanDTO = new DocumentScanDTO();
        documentScanDTO.setNom("Akharraz");
        documentScanDTO.setPrenom("Sara");
        documentScanDTO.setCin("AB123456");
        documentScanDTO.setType(DocumentScanType.CIN);

        documentScan = new DocumentScan();
        documentScan.setId(1L);
        documentScan.setNom("Akharraz");
        documentScan.setPrenom("Sara");
        documentScan.setCin("AB123456");

        checkIn = new CheckIn();
        checkIn.setId(1L);
        checkIn.setReservation(reservation);
        checkIn.setDocumentScan(documentScan);
        checkIn.setStatus(CheckInStatus.EN_ATTENTE);
    }

    @Test
    void testValiderScan_WithValidDocument_ReturnsTrue() {

        when(documentScanFactory.createAndSave(documentScanDTO)).thenReturn(documentScan);
        when(checkInFactory.createCheckIn(reservation, documentScan, CheckInStatus.EN_ATTENTE))
                .thenReturn(checkIn);

        Boolean result = checkInService.validerScan(reservation, documentScanDTO);
        assertTrue(result);

        verify(documentScanValidator).validateDocument(reservation, documentScanDTO);
        verify(documentScanFactory).createAndSave(documentScanDTO);
        verify(checkInFactory).createCheckIn(reservation, documentScan, CheckInStatus.EN_ATTENTE);
        verify(notificationManager).notifyUserCheckInPending(user.getId());
    }

    @Test
    void testGetDocumentByCheckin_WithValidId_ReturnsDocumentScan() {
        Long checkInId = 1L;
        when(checkInFinder.findById(checkInId)).thenReturn(checkIn);

        DocumentScan result = checkInService.getDocumentByCheckin(checkInId);

        assertNotNull(result);
        assertEquals(documentScan, result);
        verify(checkInFinder).findById(checkInId);
    }

    @Test
    void testGetDocumentByCheckin_WithInvalidId_ThrowsCheckInNotFoundException() {

        Long checkInId =1L;
        when(checkInFinder.findById(checkInId))
                .thenThrow(new CheckInNotFoundException("check_in non effectué!"));

        CheckInNotFoundException exception = assertThrows(
                CheckInNotFoundException.class,
                () -> checkInService.getDocumentByCheckin(checkInId)
        );

        assertEquals("check_in non effectué!", exception.getMessage());
        verify(checkInFinder).findById(checkInId);
    }


    @Test
    void testValiderCheckIn_WithValidCheckIn_ReturnsTrue() {

        when(checkInFinder.findByReservation(reservation)).thenReturn(checkIn);

        Boolean result = checkInService.validerCheckIn(reservation);
        assertTrue(result);
        verify(checkInFinder).findByReservation(reservation);
        verify(checkInValidator).validateForConfirmation(checkIn);
        verify(factureService).payerFactureCheckIn(reservation);
        verify(checkInStatusManager).updateStatus(checkIn, CheckInStatus.VALIDE);
        verify(reservationConfirmationManager).confirmReservation(reservation);
        verify(notificationManager).notifyUserReservationConfirmed(user.getId(), reservation.getId());
        verify(notificationManager).notifyStaffCheckInValidated(reservation.getId());
    }

    @Test
    void testValiderCheckIn_WithoutDocumentScan_ThrowsDocumentNotScannedException() {

        when(checkInFinder.findByReservation(reservation)).thenReturn(checkIn);
        doThrow(new DocumentNotScannedException("Le scan du document n'a pas été effectué."))
                .when(checkInValidator).validateForConfirmation(checkIn);

        DocumentNotScannedException exception = assertThrows(
                DocumentNotScannedException.class,
                () -> checkInService.validerCheckIn(reservation)
        );

        assertEquals("Le scan du document n'a pas été effectué.", exception.getMessage());
        verify(checkInValidator).validateForConfirmation(checkIn);
        verify(factureService, never()).payerFactureCheckIn((Reservation) any());
        verify(checkInStatusManager, never()).updateStatus(any(), any());
        verify(reservationConfirmationManager, never()).confirmReservation(any());
    }

    @Test
    void testCheckinsForToday_WithMultipleCheckIns_ReturnsAllCheckIns() {

        LocalDate today = LocalDate.now();
        CheckIn checkIn2 = new CheckIn();
        checkIn2.setId(2L);
        List<CheckIn> expectedCheckIns = Arrays.asList(checkIn, checkIn2);

        when(checkInFinder.findCheckinsForToday(today)).thenReturn(expectedCheckIns);

        List<CheckIn> result = checkInService.checkinsForToday(today);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedCheckIns, result);
        verify(checkInFinder).findCheckinsForToday(today);
    }

    @Test
    void testCheckinsForToday_WithNoCheckIns_ReturnsEmptyList() {
        LocalDate today = LocalDate.now();
        when(checkInFinder.findCheckinsForToday(today)).thenReturn(Collections.emptyList());

        List<CheckIn> result = checkInService.checkinsForToday(today);

        assertNotNull(result);
        verify(checkInFinder).findCheckinsForToday(today);
    }


    @Test
    void testGetCheckInByReservation_WithValidId_ReturnsCheckIn() {

        Long reservationId = 100L;
        when(checkInFinder.findByReservationId(reservationId)).thenReturn(checkIn);

        CheckIn result = checkInService.getCheckInByReservation(reservationId);

        assertNotNull(result);
        assertEquals(checkIn, result);
        verify(checkInFinder).findByReservationId(reservationId);
    }

    @Test
    void testGetCheckInByReservation_WithNonExistentReservation_ReturnsNull() {

        Long reservationId = 1L;
        when(checkInFinder.findByReservationId(reservationId)).thenReturn(null);

        CheckIn result = checkInService.getCheckInByReservation(reservationId);

        assertNull(result);
        verify(checkInFinder).findByReservationId(reservationId);
    }

    @Test
    void testGetStatusCheckIn_WithValidReservation_ReturnsStatus() {

        Long reservationId = 100L;
        when(reservationFinder.findReservationById(reservationId)).thenReturn(reservation);
        when(checkInFinder.findByReservation(reservation)).thenReturn(checkIn);

        CheckInStatus result = checkInService.getStatusCheckIn(reservationId);

        assertEquals(CheckInStatus.EN_ATTENTE, result);
        verify(reservationFinder).findReservationById(reservationId);
        verify(checkInFinder).findByReservation(reservation);
    }

    @Test
    void testGetStatusCheckIn_WithNonExistentReservation_ThrowsReservationNotFoundException() {

        Long reservationId = 1L;
        when(reservationFinder.findReservationById(reservationId))
                .thenThrow(new ReservationNotFoundException("Réservation non trouvée avec l'id : 1"));

        ReservationNotFoundException ex = assertThrows(
                ReservationNotFoundException.class,
                () -> checkInService.getStatusCheckIn(reservationId)
        );

        assertEquals("Réservation non trouvée avec l'id : 1",ex.getMessage());
        verify(reservationFinder).findReservationById(reservationId);
        verify(checkInFinder, never()).findByReservation(any());
    }
    @Test
    void testValiderCheckinReception_WithValidCheckIn_UpdatesSuccessfully() {

        Long checkInId = 1L;
        when(checkInFinder.findById(checkInId)).thenReturn(checkIn);
        when(reservationFinder.findReservationById(reservation.getId())).thenReturn(reservation);

        checkInService.validerCheckinReception(checkInId);

        verify(checkInFinder).findById(checkInId);
        verify(reservationFinder).findReservationById(reservation.getId());
        verify(factureService).payerFactureCheckInCache(reservation);
        verify(checkInStatusManager).updateStatus(checkIn, CheckInStatus.VALIDE);
        verify(notificationManager).notifyUserReservationConfirmed(user.getId(), reservation.getId());
        verify(notificationManager).notifyStaffCheckInValidated(reservation.getId());
    }

    @Test
    void testValiderCheckinReception_WithNonExistentCheckIn_ThrowsCheckInNotFoundException() {

        Long checkInId = 1L;
        when(checkInFinder.findById(checkInId))
                .thenThrow(new CheckInNotFoundException("check_in non effectué!"));

        CheckInNotFoundException exception = assertThrows(
                CheckInNotFoundException.class,
                () -> checkInService.validerCheckinReception(checkInId)
        );

        assertEquals("check_in non effectué!", exception.getMessage());
        verify(checkInFinder).findById(checkInId);
        verify(factureService, never()).payerFactureCheckInCache(any());
        verify(checkInStatusManager, never()).updateStatus(any(), any());
    }

    @Test
    void testAjoutercheckinReception_WithValidData_CreatesCheckInSuccessfully() {

        Long reservationId = 100L;
        when(reservationFinder.findReservationById(reservationId)).thenReturn(reservation);
        when(checkInFactory.createCheckIn(reservation, documentScan, CheckInStatus.VALIDE))
                .thenReturn(checkIn);

        checkInService.ajoutercheckinReception(reservationId, documentScan);

        verify(reservationFinder).findReservationById(reservationId);
        verify(documentScanRepository).save(documentScan);
        verify(factureService).payerFactureCheckInCache(reservation);
        verify(checkInFactory).createCheckIn(reservation, documentScan, CheckInStatus.VALIDE);
        verify(reservationConfirmationManager).confirmReservation(reservation);
        verify(notificationManager).notifyStaffCheckInAdded(reservation.getId());
    }

    @Test
    void testAjoutercheckinReception_WithNonExistentReservation_ThrowsReservationNotFoundException() {

        Long reservationId = 1L;
        when(reservationFinder.findReservationById(reservationId))
                .thenThrow(new ReservationNotFoundException("Reservation non trouvée"));

        ReservationNotFoundException exception = assertThrows(
                ReservationNotFoundException.class,
                () -> checkInService.ajoutercheckinReception(reservationId, documentScan)
        );

        assertEquals("Reservation non trouvée", exception.getMessage());
        verify(reservationFinder).findReservationById(reservationId);
        verify(documentScanRepository, never()).save(any());
        verify(factureService, never()).payerFactureCheckInCache(any());
        verify(checkInFactory, never()).createCheckIn(any(), any(), any());
    }

}