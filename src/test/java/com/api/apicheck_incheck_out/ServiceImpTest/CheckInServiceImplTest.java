package com.api.apicheck_incheck_out.ServiceImpTest;

import com.api.apicheck_incheck_out.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.Entity.*;
import com.api.apicheck_incheck_out.Enums.*;
import com.api.apicheck_incheck_out.Mapper.DocumentScanMapper;
import com.api.apicheck_incheck_out.Repository.*;
import com.api.apicheck_incheck_out.Service.FactureService;
import com.api.apicheck_incheck_out.Service.Impl.CheckInServiceImpl;
import com.api.apicheck_incheck_out.Service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckInServiceImplTest {

    @Mock
    CheckInRepository checkInRepository;
    @Mock
    DocumentScanRepository documentScanRepository;
    @Mock
    FactureService factureService;
    @Mock
    NotificationService notificationService;
    @Mock
    ReservationRepository reservationRepository;
    @Mock
    ChambreRepository chambreRepository;
    @Mock
    FactureRepository factureRepository;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    ChambreReservationRepository chambreReservationRepository;
    @Mock
    DocumentScanMapper documentScanMapper;
    @Mock
    ReservationServiceRepository reservationServiceRepository;

    @InjectMocks
    CheckInServiceImpl checkInService;

    User user;
    Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);


        user = new User();
        user.setId(1L);
        user.setNom("sara");
        user.setPrenom("akharraz");
        user.setCin("AA123456");


        reservation = new Reservation();
        reservation.setId(10L);
        reservation.setUser(user);
        reservation.setStatus(ReservationStatus.En_Attente);
        reservation.setChambreReservations(new ArrayList<>());
    }

    @Test
    void testValiderScan_Success() {
        DocumentScanDTO doc = new DocumentScanDTO();
        doc.setNom("sara");
        doc.setPrenom("akharraz");
        doc.setCin("AA123456");
        doc.setType(DocumentScanType.CIN);


        when(documentScanRepository.save(any(DocumentScan.class))).thenAnswer(i -> i.getArgument(0));
        when(checkInRepository.save(any(Check_In.class))).thenAnswer(i -> i.getArgument(0));

        Boolean result = checkInService.validerScan(reservation, doc);

        assertTrue(result);
        verify(documentScanRepository, times(1)).save(any(DocumentScan.class));
        verify(checkInRepository, times(1)).save(any(Check_In.class));
        verify(notificationService, times(1)).notifier(eq(user.getId()), anyString());
    }

    @Test
    void testGetDocumentByCheckin_Success() {
        DocumentScan doc = new DocumentScan();
        doc.setNom("sara");

        Check_In checkIn = new Check_In();
        checkIn.setDocumentScan(doc);

        when(checkInRepository.findById(1L)).thenReturn(Optional.of(checkIn));

        DocumentScan result = checkInService.getDocumentByCheckin(1L);

        assertNotNull(result);
        assertEquals("sara", result.getNom());
    }

    @Test
    void testValiderCheckIn_Success() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);

        User user = new User();
        user.setId(1L);
        reservation.setUser(user);

        DocumentScan doc = new DocumentScan();
        Check_In checkIn = new Check_In();
        checkIn.setDocumentScan(doc);
        checkIn.setStatus(CheckInStatus.En_Attente);

        ChambreReservation chambre = new ChambreReservation();
        chambre.setReservation(reservation);
        chambre.setStatut(ChambreStatut.DISPONIBLE);
        reservation.setChambreReservations(List.of(chambre));

        ReservationServices service = new ReservationServices();
        service.setReservation(reservation);
        service.setPaiementStatus(PaiementStatus.en_attente);
        reservation.setServiceList(List.of(service));

        when(checkInRepository.findByReservation(reservation)).thenReturn(Optional.of(checkIn));
        when(checkInRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(chambreReservationRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));
        when(reservationServiceRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        Boolean result = checkInService.validerCheckIn(reservation);

        assertTrue(result);
        assertEquals(CheckInStatus.Validé, checkIn.getStatus());
        assertEquals(ReservationStatus.Confirmee, reservation.getStatus());
        assertEquals(ChambreStatut.OCCUPEE, chambre.getStatut());
        assertEquals(PaiementStatus.paye, service.getPaiementStatus());
    }








    @Test
    void testGetCheckInByReservation_Success() {
        Check_In checkIn = new Check_In();

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));
        when(checkInRepository.findByReservation(reservation)).thenReturn(Optional.of(checkIn));

        Check_In result = checkInService.getCheckInByReservation(10L);

        assertNotNull(result);
    }

    @Test
    void testGetStatusCheckIn_Success() {
        Check_In checkIn = new Check_In();
        checkIn.setStatus(CheckInStatus.En_Attente);

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));
        when(checkInRepository.findByReservation(reservation)).thenReturn(Optional.of(checkIn));

        CheckInStatus status = checkInService.getStatusCheckIn(10L);

        assertEquals(CheckInStatus.En_Attente, status);
    }

    @Test
    void testValiderCheckinReception_Success() {
        Check_In checkIn = new Check_In();
        checkIn.setReservation(reservation);

        when(checkInRepository.findById(1L)).thenReturn(Optional.of(checkIn));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
        when(checkInRepository.save(any(Check_In.class))).thenAnswer(i -> i.getArgument(0));

        checkInService.validerCheckinReception(1L);

        verify(factureService, times(1)).payerFactureCheckInCache(reservation);
        verify(checkInRepository, times(1)).save(checkIn);
        assertEquals(CheckInStatus.Validé, checkIn.getStatus());
    }

    @Test
    void testAjoutercheckinReception_Success() {
        DocumentScan doc = new DocumentScan();

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));
        when(documentScanRepository.save(doc)).thenReturn(doc);
        when(checkInRepository.save(any(Check_In.class))).thenAnswer(i -> i.getArgument(0));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        checkInService.ajoutercheckinReception(10L, doc);

        verify(factureService, times(1)).payerFactureCheckInCache(reservation);
        verify(documentScanRepository, times(1)).save(doc);
        verify(checkInRepository, times(1)).save(any(Check_In.class));
        verify(reservationRepository, times(1)).save(reservation);
        assertEquals(ReservationStatus.Confirmee, reservation.getStatus());
    }
}
