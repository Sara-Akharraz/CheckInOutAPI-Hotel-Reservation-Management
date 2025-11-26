package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.*;
import com.api.apicheck_incheck_out.exceptionhandling.*;
import com.api.apicheck_incheck_out.mapper.DocumentScanMapper;
import com.api.apicheck_incheck_out.repository.*;
import com.api.apicheck_incheck_out.service.FactureService;
import com.api.apicheck_incheck_out.service.impl.CheckInServiceImpl;
import com.api.apicheck_incheck_out.service.NotificationService;
import com.api.apicheck_incheck_out.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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
    UserServiceImpl userService;
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
        reservation.setStatus(ReservationStatus.EN_ATTENTE);
        reservation.setChambreReservations(new ArrayList<>());
    }

    @Test
    void testValiderScan_SuccessCIN() {
        DocumentScanDTO doc = new DocumentScanDTO();
        doc.setNom("sara");
        doc.setPrenom("akharraz");
        doc.setCin("AA123456");
        doc.setType(DocumentScanType.CIN);


        Boolean result = checkInService.validerScan(reservation, doc);

        assertTrue(result);
        verify(documentScanRepository, times(1)).save(any(DocumentScan.class));
        verify(checkInRepository, times(1)).save(any(CheckIn.class));
        verify(notificationService, times(1)).notifier(eq(user.getId()), anyString());
    }
    @Test
    void testValiderScan_SuccessPassport() {
        DocumentScanDTO doc = new DocumentScanDTO();
        doc.setNom("Sara");
        doc.setPrenom("Akharraz");
        doc.setType(DocumentScanType.PASSPORT);
        doc.setPassport("P987654");


        User us = new User();
        us.setId(1L);
        us.setNom("Sara");
        us.setPrenom("Akharraz");
        us.setNumeroPassport("P987654");

        Reservation res = new Reservation();
        res.setUser(us);

        Boolean result = checkInService.validerScan(res, doc);

        assertTrue(result);
        verify(documentScanRepository, times(1)).save(any(DocumentScan.class));
        verify(checkInRepository, times(1)).save(any(CheckIn.class));
        verify(notificationService, times(1)).notifier(eq(us.getId()), anyString());
    }
    @Test
    void testValiderScan_InvalidName() {
        DocumentScanDTO doc = new DocumentScanDTO();
        doc.setNom("Wrong");
        doc.setPrenom("Name");
        doc.setType(DocumentScanType.CIN);
        doc.setCin("AB123456");

        InvalidNameException ex = assertThrows(InvalidNameException.class,
                () -> checkInService.validerScan(reservation, doc));

        assertEquals("Nom ou prénom incorrect !", ex.getMessage());
    }
    @Test
    void testValiderScan_InvalidCIN() {
        DocumentScanDTO doc = new DocumentScanDTO();
        doc.setNom("Sara");
        doc.setPrenom("Akharraz");
        doc.setType(DocumentScanType.CIN);
        doc.setCin("WRONGCIN");

        InvalidCINException ex = assertThrows(InvalidCINException.class,
                () -> checkInService.validerScan(reservation, doc));

        assertEquals("CIN non valide !", ex.getMessage());
    }
    @Test
    void testValiderScan_InvalidPassport() {
        DocumentScanDTO doc = new DocumentScanDTO();
        doc.setNom("Sara");
        doc.setPrenom("Akharraz");
        doc.setType(DocumentScanType.PASSPORT);
        doc.setPassport("WRONGPASS");

        InvalidPassportException ex = assertThrows(InvalidPassportException.class,
                () -> checkInService.validerScan(reservation, doc));

        assertEquals("Passport non valide !", ex.getMessage());
    }
    @Test
    void testGetDocumentByCheckin_Success() {

        Long checkInId = 1L;
        DocumentScan documentScan = new DocumentScan();
        documentScan.setId(100L);

        CheckIn checkIn = new CheckIn();
        checkIn.setId(checkInId);
        checkIn.setDocumentScan(documentScan);

        when(checkInRepository.findById(checkInId)).thenReturn(Optional.of(checkIn));


        DocumentScan result = checkInService.getDocumentByCheckin(checkInId);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(checkInRepository, times(1)).findById(checkInId);
    }

    @Test
    void testGetDocumentByCheckin_CheckInNotFound() {
        Long checkInId = 2L;

        when(checkInRepository.findById(checkInId)).thenReturn(Optional.empty());


        CheckInNotFoundException ex = assertThrows(CheckInNotFoundException.class,
                () -> checkInService.getDocumentByCheckin(checkInId));

        assertEquals("Checkin introuvable avec l'id " + checkInId, ex.getMessage());
        verify(checkInRepository, times(1)).findById(checkInId);
    }

    @Test
    void testGetCheckInByReservation_Success() {
        CheckIn checkIn = new CheckIn();

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));
        when(checkInRepository.findByReservation(reservation)).thenReturn(Optional.of(checkIn));

        CheckIn result = checkInService.getCheckInByReservation(10L);

        assertNotNull(result);
    }

    @Test
    void testGetStatusCheckIn_Success() {
        CheckIn checkIn = new CheckIn();
        checkIn.setStatus(CheckInStatus.EN_ATTENTE);

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));
        when(checkInRepository.findByReservation(reservation)).thenReturn(Optional.of(checkIn));

        CheckInStatus status = checkInService.getStatusCheckIn(10L);

        assertEquals(CheckInStatus.EN_ATTENTE, status);
    }

    @Test
    void testCheckinsForToday() {
        LocalDate today = LocalDate.now();

        Reservation res1 = new Reservation();
        CheckIn checkIn1 = new CheckIn();
        res1.setCheckIn(checkIn1);

        Reservation res2 = new Reservation();
        CheckIn checkIn2 = new CheckIn();
        res2.setCheckIn(checkIn2);

        Reservation res3 = new Reservation();

        when(reservationRepository.findByDateDebut(today))
                .thenReturn(List.of(res1, res2, res3));

        List<CheckIn> result = checkInService.checkinsForToday(today);

        assertEquals(2, result.size());
        assertTrue(result.contains(checkIn1));
        assertTrue(result.contains(checkIn2));
        verify(reservationRepository, times(1)).findByDateDebut(today);
    }
    @Test
    void testGetCheckInByReservation() {
        Long reservationId = 1L;
        Reservation res = new Reservation();
        CheckIn checkIn = new CheckIn();
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(res));
        when(checkInRepository.findByReservation(res)).thenReturn(Optional.of(checkIn));

        CheckIn result = checkInService.getCheckInByReservation(reservationId);

        assertNotNull(result);
        assertEquals(checkIn, result);
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(checkInRepository, times(1)).findByReservation(res);
    }

    @Test
    void testGetCheckInByReservation_ReservationNotFound() {
        Long reservationId = 2L;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> checkInService.getCheckInByReservation(reservationId));

        assertEquals("Réservation non trouvée avec l'id : " + reservationId, ex.getMessage());
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(checkInRepository, never()).findByReservation(any());
    }

    @Test
    void testGetCheckInByReservation_NoCheckIn() {
        Long reservationId = 3L;
        Reservation res = new Reservation();
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(res));
        when(checkInRepository.findByReservation(res)).thenReturn(Optional.empty());

        CheckIn result = checkInService.getCheckInByReservation(reservationId);

        assertNull(result);
        verify(checkInRepository, times(1)).findByReservation(res);
    }
    @Test
    void testValiderCheckinReception_ReservationNotFound(){
        Reservation res=new Reservation();
        res.setId(1L);
        CheckIn checkIn=new CheckIn();
        checkIn.setId(1L);
        checkIn.setReservation(res);
        when(checkInRepository.findById(1L)).thenReturn(Optional.of(checkIn));

        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());
        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()->checkInService.validerCheckinReception(1L));

        assertEquals("Reservation non trouvée",ex.getMessage());
        verify(reservationRepository,times(1)).findById(1L);
    }
    @Test
    void testValiderCheckinReception_CheckInNotFound(){

        when(checkInRepository.findById(1L)).thenReturn(Optional.empty());
        CheckInNotFoundException ex=assertThrows(CheckInNotFoundException.class,()->checkInService.validerCheckinReception(1L));
        assertEquals("check_in non effectué!",ex.getMessage());
        verify(checkInRepository,times(1)).findById(1L);
    }
    @Test
    void testValiderCheckinReception() {

        User us = new User();
        us.setId(1L);
        Reservation res = new Reservation();
        res.setId(1L);
        res.setUser(us);

        CheckIn checkIn = new CheckIn();
        checkIn.setId(1L);
        checkIn.setReservation(res);
        checkIn.setStatus(CheckInStatus.EN_ATTENTE);

        User admin1 = new User();
        admin1.setId(2L);
        User admin2 = new User();
        admin2.setId(3L);
        List<User> admins = List.of(admin1, admin2);

        UserDto recep1 = new UserDto();
        recep1.setId(4L);
        UserDto recep2 = new UserDto();
        recep2.setId(5L);
        List<UserDto> receps = List.of(recep1, recep2);

        when(checkInRepository.findById(1L)).thenReturn(Optional.of(checkIn));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(res));
        when(userService.getAdmins()).thenReturn(admins);
        when(userService.getReceptionists()).thenReturn(receps);


        checkInService.validerCheckinReception(1L);

        assertEquals(CheckInStatus.VALIDE, checkIn.getStatus());
        verify(checkInRepository, times(1)).findById(1L);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(factureService, times(1)).payerFactureCheckInCache(captor.capture());
        assertEquals(1L, captor.getValue().getId());


        verify(notificationService, times(1))
                .notifier(eq(us.getId()), contains("Réservation Confirmée ,Numéro de reservation :1"));

        for (User admin : admins) {
            verify(notificationService, times(1))
                    .notifier(eq(admin.getId()), contains("Check-In validé pour la réservation numéro : 1"));
        }

        for (UserDto recep : receps) {
            verify(notificationService, times(1))
                    .notifier(eq(recep.getId()), contains("Check-In validé pour la réservation numéro : 1"));
        }
    }
    @Test
    void testGetStatusCheckIn_ReservationNotFoundException(){
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());
        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()->checkInService.getStatusCheckIn(1L));
        assertEquals("Réservation non trouvée avec l'id : 1",ex.getMessage());
        verify(reservationRepository,times(1)).findById(1L);
    }
    @Test
    void testGetStatusCheckIn_CheckInNotFoundException(){
        Reservation res=new Reservation();
        res.setId(1L);
        CheckIn checkIn=new CheckIn();
        checkIn.setId(1L);
        checkIn.setReservation(res);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(res));
        when(checkInRepository.findByReservation(res)).thenReturn(Optional.empty());

        CheckInNotFoundException ex=assertThrows(CheckInNotFoundException.class,()->checkInService.getStatusCheckIn(1L));
        assertEquals("Aucun check-in trouvé pour la réservation avec l'id : 1",ex.getMessage());

        verify(reservationRepository,times(1)).findById(1L);
        verify(checkInRepository,times(1)).findByReservation(res);
    }
    @Test
    void testAjouterCheckinReception_ReservationNotFound(){
        DocumentScan doc=new DocumentScan();
        doc.setId(1L);
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());
        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()->checkInService.ajoutercheckinReception(1L,doc));
        assertEquals("Reservation non trouvée",ex.getMessage());
        verify(reservationRepository,times(1)).findById(1L);
    }
    @Test
    void testAjouterCheckInReception_success(){

        DocumentScan doc=new DocumentScan();
        doc.setId(1L);

        User us = new User();
        us.setId(1L);

        Reservation res = new Reservation();
        res.setId(1L);
        res.setUser(us);
        res.setStatus(ReservationStatus.EN_ATTENTE);


        User admin1 = new User();
        admin1.setId(2L);
        User admin2 = new User();
        admin2.setId(3L);
        List<User> admins = List.of(admin1, admin2);

        UserDto recep1 = new UserDto();
        recep1.setId(4L);
        UserDto recep2 = new UserDto();
        recep2.setId(5L);
        List<UserDto> receps = List.of(recep1, recep2);


        when(reservationRepository.findById(1L)).thenReturn(Optional.of(res));
        when(userService.getAdmins()).thenReturn(admins);
        when(userService.getReceptionists()).thenReturn(receps);


        checkInService.ajoutercheckinReception(1L,doc);

        assertEquals(ReservationStatus.CONFIRMEE,res.getStatus());


        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(factureService, times(1)).payerFactureCheckInCache(captor.capture());
        assertEquals(1L, captor.getValue().getId());


        for (User admin : admins) {
            verify(notificationService, times(1))
                    .notifier(eq(admin.getId()), contains("Check-In ajouté pour la réservation numéro : 1"));
        }

        for (UserDto recep : receps) {
            verify(notificationService, times(1))
                    .notifier(eq(recep.getId()), contains("Check-In ajouté pour la réservation numéro : 1"));
        }
        verify(documentScanRepository, times(1)).save(doc);
        verify(checkInRepository, times(1)).save(any(CheckIn.class));
    }
    @Test
    void testValiderCheckIn_CheckInNotFoundException(){
        Reservation res=new Reservation();
        res.setId(1L);
        CheckIn checkIn=new CheckIn();
        checkIn.setReservation(res);

        when(checkInRepository.findByReservation(res)).thenReturn(Optional.empty());

        CheckInNotFoundException ex=assertThrows(CheckInNotFoundException.class,()->checkInService.validerCheckIn(res));
        assertEquals("Aucun check-in trouvé pour cette réservation.",ex.getMessage());
        verify(checkInRepository,times(1)).findByReservation(res);
    }
    @Test
    void testValiderCheckIn_DocumentNotScannedException(){
        Reservation res=new Reservation();
        res.setId(1L);
        CheckIn checkIn=new CheckIn();
        checkIn.setReservation(res);

        when(checkInRepository.findByReservation(res)).thenReturn(Optional.of(checkIn));

        DocumentNotScannedException ex=assertThrows(DocumentNotScannedException.class,()->checkInService.validerCheckIn(res));
        assertEquals("Le scan du document n'a pas été effectué.",ex.getMessage());

        verify(checkInRepository,times(1)).findByReservation(res);

    }
    @Test
    void testValiderCheckIn_InvalidCheckInStatusException(){
        DocumentScan doc=new DocumentScan();
        doc.setId(1L);
        Reservation res=new Reservation();
        res.setId(1L);

        CheckIn checkIn=new CheckIn();
        checkIn.setReservation(res);
        checkIn.setDocumentScan(doc);
        checkIn.setStatus(CheckInStatus.VALIDE);

        when(checkInRepository.findByReservation(res)).thenReturn(Optional.of(checkIn));
        InvalidCheckInStatusException ex=assertThrows(InvalidCheckInStatusException.class,()->checkInService.validerCheckIn(res));
        assertEquals("Le check-in n'est pas en attente.",ex.getMessage());

        verify(checkInRepository,times(1)).findByReservation(res);

    }
    @Test
    void testValiderCheckIn_success(){

        DocumentScan doc=new DocumentScan();
        doc.setId(1L);

        User us = new User();
        us.setId(1L);

        Reservation res = new Reservation();
        res.setId(1L);
        res.setUser(us);
        res.setStatus(ReservationStatus.EN_ATTENTE);

        CheckIn checkIn=new CheckIn();
        checkIn.setReservation(res);
        checkIn.setDocumentScan(doc);
        checkIn.setStatus(CheckInStatus.EN_ATTENTE);

        User admin1 = new User();
        admin1.setId(2L);
        User admin2 = new User();
        admin2.setId(3L);
        List<User> admins = List.of(admin1, admin2);

        UserDto recep1 = new UserDto();
        recep1.setId(4L);
        UserDto recep2 = new UserDto();
        recep2.setId(5L);
        List<UserDto> receps = List.of(recep1, recep2);

        Chambre chambre1=new Chambre();
        chambre1.setId(1L);
        Chambre chambre2=new Chambre();
        chambre2.setId(2L);

        ChambreReservation cr1=new ChambreReservation();
        cr1.setChambre(chambre1);
        cr1.setReservation(res);
        ChambreReservation cr2=new ChambreReservation();
        cr2.setChambre(chambre2);
        cr2.setReservation(res);


        List<ChambreReservation> chambreReservations=List.of(cr1,cr2);
        res.setChambreReservations(chambreReservations);

        Services service1=new Services();
        service1.setId(1L);
        Services service2=new Services();
        service2.setId(2L);

        ReservationServices rs1=new ReservationServices();
        rs1.setService(service1);
        rs1.setReservation(res);
        ReservationServices rs2=new ReservationServices();
        rs2.setService(service2);
        rs2.setReservation(res);

        List<ReservationServices> reservationServices=List.of(rs1,rs2);
        res.setServiceList(reservationServices);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(res));
        when(checkInRepository.findByReservation(res)).thenReturn(Optional.of(checkIn));
        when(userService.getAdmins()).thenReturn(admins);
        when(userService.getReceptionists()).thenReturn(receps);


        checkInService.validerCheckIn(res);

        assertEquals(ReservationStatus.CONFIRMEE,res.getStatus());
        assertEquals(CheckInStatus.VALIDE,checkIn.getStatus());


        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(factureService, times(1)).payerFactureCheckIn(captor.capture());
        assertEquals(1L, captor.getValue().getId());

        for (ChambreReservation cr : chambreReservations) {
            assertEquals(ChambreStatut.OCCUPEE,cr.getStatut());
        }
        for (ReservationServices rservice : reservationServices) {
            assertEquals(PaiementStatus.PAYE,rservice.getPaiementStatus());
        }

        verify(notificationService, times(1))
                .notifier(eq(us.getId()), contains("Réservation Confirmée ,Numéro de reservation :1"));

        for (User admin : admins) {
            verify(notificationService, times(1))
                    .notifier(eq(admin.getId()), contains("Check-In validé pour la réservation numéro : 1"));
        }

        for (UserDto recep : receps) {
            verify(notificationService, times(1))
                    .notifier(eq(recep.getId()), contains("Check-In validé pour la réservation numéro : 1"));
        }

        verify(checkInRepository, times(1)).save(any(CheckIn.class));

    }

}

