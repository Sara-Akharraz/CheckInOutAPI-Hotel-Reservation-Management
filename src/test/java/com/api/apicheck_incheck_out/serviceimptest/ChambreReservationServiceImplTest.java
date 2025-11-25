package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreReservationNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.InvalidReservationStatusException;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
import com.api.apicheck_incheck_out.repository.ChambreRepository;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.impl.ChambreReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChambreReservationServiceImplTest {

    @Mock
    private ChambreReservationRepository chambreReservationRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    ChambreReservationServiceImpl chambreReservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChambreStatut_Success() {
        Long reservationId = 1L;
        Long chambreId = 2L;

        ChambreReservation cr = new ChambreReservation();
        cr.setStatut(ChambreStatut.DISPONIBLE);

        when(chambreReservationRepository.findByReservation_IdAndChambre_Id(reservationId, chambreId))
                .thenReturn(Optional.of(cr));

        ChambreStatut statut = chambreReservationService.getChambreStatut(reservationId, chambreId);

        assertEquals(ChambreStatut.DISPONIBLE, statut);
    }

    @Test
    void testGetChambreStatut_NotFound() {
        when(chambreReservationRepository.findByReservation_IdAndChambre_Id(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chambreReservationService.getChambreStatut(1L, 1L));

        assertTrue(exception.getMessage().contains("ChambreReservation non trouvée"));
    }

    @Test
    void testSetChambreOccupee_Success() {
        Long reservationId = 1L;

        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.CONFIRMEE);

        ChambreReservation cr = new ChambreReservation();
        cr.setStatut(ChambreStatut.DISPONIBLE);

        List<ChambreReservation> chambreReservations = Collections.singletonList(cr);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(chambreReservationRepository.findByReservation_Id(reservationId)).thenReturn(chambreReservations);
        when(chambreReservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        chambreReservationService.setChambreOccupee(reservationId);

        assertEquals(ChambreStatut.OCCUPEE, cr.getStatut());
        verify(chambreReservationRepository, times(1)).save(cr);
    }

    @Test
    void testSetChambreOccupee_ReservationNotConfirmed() {
        Long reservationId = 1L;

        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        InvalidReservationStatusException exception = assertThrows(InvalidReservationStatusException.class,
                () -> chambreReservationService.setChambreOccupee(reservationId));

        assertTrue(exception.getMessage().contains("La réservation n'est pas confirmée"));
    }
    @Test
    void testSetChambreOccupee_ReservationNotFound(){
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()->chambreReservationService.setChambreOccupee(1L));

        assertEquals("Réservation non trouvée avec l'id 1",ex.getMessage());
        verify(reservationRepository,times(1)).findById(1L);

    }
    @Test
    void testSetChambreOccupee_ReservationThrowsException(){
        Reservation res=new Reservation();
        res.setId(2L);
        res.setStatus(ReservationStatus.CONFIRMEE);
        when(reservationRepository.findById(2L)).thenReturn(Optional.of(res));

        when(chambreReservationRepository.findByReservation_Id(2L)).thenReturn(List.of());

        ChambreReservationNotFoundException ex=assertThrows(ChambreReservationNotFoundException.class,()->chambreReservationService.setChambreOccupee(2L));
        assertEquals("Aucune ChambreReservation trouvée pour la réservation ID : 2",ex.getMessage());

        verify(chambreReservationRepository,times(1)).findByReservation_Id(2L);
        verify(chambreReservationRepository,never()).save(any(ChambreReservation.class));
    }

    @Test
    void testGetChambresByReservation(){
        Reservation res=new Reservation();
        res.setId(100L);
        Chambre c1= new Chambre();
        c1.setId(10L);
        Chambre c2=new Chambre();
        c2.setId(20L);
        ChambreReservation cr1 = new ChambreReservation();
        cr1.setChambre(c1);
        ChambreReservation cr2 = new ChambreReservation();
        cr2.setChambre(c2);

        when(chambreReservationRepository.findByReservation_Id(100L)).thenReturn(List.of(cr1,cr2));

        List<Chambre> chambres = chambreReservationService.getChambresByReservation(100L);

        assertEquals(2,chambres.size());
        assertEquals(10L,chambres.get(0).getId());
        assertEquals(20L,chambres.get(1).getId());

        verify(chambreReservationRepository,times(1)).findByReservation_Id(100L);
    }
    @Test
    void testGetChambresDisponibles(){
        Reservation res=new Reservation();
        res.setId(100L);
        Chambre c1= new Chambre();
        c1.setId(10L);
        Chambre c2=new Chambre();
        c2.setId(20L);
        ChambreReservation cr1 = new ChambreReservation();
        cr1.setChambre(c1);
        ChambreReservation cr2 = new ChambreReservation();
        cr2.setChambre(c2);

        when(chambreReservationRepository.findByStatut(ChambreStatut.DISPONIBLE)).thenReturn(List.of(cr1,cr2));

        List<Chambre> chambres = chambreReservationService.getChambresDisponibles();

        assertEquals(2,chambres.size());
        assertEquals(10L,chambres.get(0).getId());
        assertEquals(20L,chambres.get(1).getId());

        verify(chambreReservationRepository,times(1)).findByStatut(ChambreStatut.DISPONIBLE);

    }
    @Test
    void testSetChambreDisponible_Success() {
        Long reservationId = 1L;

        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.CONFIRMEE);

        Chambre chambre1 = new Chambre();
        chambre1.setId(10L);

        ChambreReservation cr = new ChambreReservation();
        cr.setChambre(chambre1);

        List<ChambreReservation> chambreReservations = Collections.singletonList(cr);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(chambreReservationRepository.findByReservation(reservation)).thenReturn(chambreReservations);
        when(chambreReservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        chambreReservationService.setChambreDisponible(reservationId);

        assertEquals(ChambreStatut.DISPONIBLE, cr.getStatut());
        assertEquals(ReservationStatus.TERMINEE, reservation.getStatus());

        verify(chambreReservationRepository, times(1)).save(cr);
    }
    @Test
    void testSetChambreReserved_Success() {
        Long reservationId = 1L;
        Long chambreId=10L;
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        Chambre chambre1 = new Chambre();
        chambre1.setId(10L);

        ChambreReservation cr = new ChambreReservation();
        cr.setChambre(chambre1);


        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(chambreReservationRepository.findByReservation_IdAndChambre_Id(reservationId,chambreId)).thenReturn(Optional.of(cr));
        when(chambreReservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));


        chambreReservationService.setChambreReserved(chambreId, reservationId);


        assertEquals(ChambreStatut.RESERVED, cr.getStatut());
        assertEquals(reservation, cr.getReservation());
        verify(chambreReservationRepository, times(1)).save(cr);
    }
    @Test
    void testSetChambreDisponible_ReservationNotConfirmed() {
        Long reservationId = 1L;

        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        InvalidReservationStatusException exception = assertThrows(InvalidReservationStatusException.class,
                () -> chambreReservationService.setChambreDisponible(reservationId));

        assertTrue(exception.getMessage().contains("La réservation n'est pas confirmée"));
    }
    @Test
    void testSetChambreDisponible_ReservationNotFound(){
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()->chambreReservationService.setChambreDisponible(1L));

        assertEquals("Réservation non trouvée avec l'id 1",ex.getMessage());
        verify(reservationRepository,times(1)).findById(1L);

    }
    @Test
    void testSetChambreReserved_ReservationNotFound(){

        when(chambreReservationRepository.findByReservation_IdAndChambre_Id(1L, 10L))
                .thenReturn(Optional.of(new ChambreReservation()));

        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()->chambreReservationService.setChambreReserved(10L,1L));

        assertEquals("Réservation non trouvée avec l'id 1",ex.getMessage());
        verify(reservationRepository,times(1)).findById(1L);

    }
    @Test
    void testSetChambreReserved_ReservationNotConfirmed() {
        when(chambreReservationRepository.findByReservation_IdAndChambre_Id(1L, 10L))
                .thenReturn(Optional.of(new ChambreReservation()));
        Long reservationId = 1L;

        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.CONFIRMEE);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        InvalidReservationStatusException exception = assertThrows(InvalidReservationStatusException.class,
                () -> chambreReservationService.setChambreReserved(10L,reservationId));

        assertTrue(exception.getMessage().contains("La réservation n'est pas en attente, statut actuel : "+ reservation.getStatus()));
    }
    @Test
    void testSetChambreReserved_ChambreReservationNotFound(){
        Long reservationId = 1L;
        Long chambreId = 10L;
        when(chambreReservationRepository.findByReservation_IdAndChambre_Id(reservationId, chambreId))
                .thenReturn(Optional.empty());

        ChambreReservationNotFoundException ex=assertThrows(ChambreReservationNotFoundException.class,()->chambreReservationService.setChambreReserved(chambreId, reservationId));

        assertEquals("ChambreReservation non trouvée pour la réservation ID : "+ reservationId + " et chambre ID : " + chambreId,ex.getMessage());
        verify(chambreReservationRepository, times(1))
                .findByReservation_IdAndChambre_Id(reservationId, chambreId);

    }
    @Test
    void testFindChambresDisponibles() {

        String dateDebut = LocalDate.now().toString();
        String dateFin = LocalDate.now().plusDays(2).toString();
        Integer capacite = 2;
        ChambreType type = ChambreType.DOUBLE;
        String etage = "1";


        Chambre chambre1 = new Chambre();
        chambre1.setId(1L);
        chambre1.setCapacite(2);
        chambre1.setType(ChambreType.DOUBLE);
        chambre1.setEtage("1");

        Chambre chambre2 = new Chambre();
        chambre2.setId(2L);
        chambre2.setCapacite(1);
        chambre2.setType(ChambreType.SINGLE);
        chambre2.setEtage("2");


        when(chambreRepository.findAll()).thenReturn(List.of(chambre1, chambre2));


        Reservation res = new Reservation();
        res.setDateDebut(LocalDate.now());
        res.setDateFin(LocalDate.now().plusDays(1));

        ChambreReservation cr = new ChambreReservation();
        cr.setChambre(chambre2);
        cr.setReservation(res);

        when(chambreReservationRepository.findByChambre_Id(1L)).thenReturn(List.of());
        when(chambreReservationRepository.findByChambre_Id(2L)).thenReturn(List.of(cr));


        List<Chambre> disponibles = chambreReservationService.findChambresDisponibles(dateDebut, dateFin, capacite, type, etage);


        assertEquals(1, disponibles.size());
        assertEquals(1L, disponibles.get(0).getId());
    }
    @Test
    void testFindChambresDisponibles_AllFilters() {

        String dateDebut = LocalDate.now().toString();
        String dateFin = LocalDate.now().plusDays(2).toString();
        Integer capacite = 2;
        ChambreType type = ChambreType.DOUBLE;
        String etage = "1";


        Chambre chambre1 = new Chambre();
        chambre1.setId(1L);
        chambre1.setCapacite(1);
        chambre1.setType(ChambreType.DOUBLE);
        chambre1.setEtage("1");


        Chambre chambre2 = new Chambre();
        chambre2.setId(2L);
        chambre2.setCapacite(2);
        chambre2.setType(ChambreType.SINGLE);
        chambre2.setEtage("1");


        Chambre chambre3 = new Chambre();
        chambre3.setId(3L);
        chambre3.setCapacite(2);
        chambre3.setType(ChambreType.DOUBLE);
        chambre3.setEtage("2");


        Chambre chambre4 = new Chambre();
        chambre4.setId(4L);
        chambre4.setCapacite(2);
        chambre4.setType(ChambreType.DOUBLE);
        chambre4.setEtage("1");

        Reservation res = new Reservation();
        res.setDateDebut(LocalDate.now().plusDays(1));
        res.setDateFin(LocalDate.now().plusDays(3));

        ChambreReservation cr = new ChambreReservation();
        cr.setChambre(chambre4);
        cr.setReservation(res);


        Chambre chambre5 = new Chambre();
        chambre5.setId(5L);
        chambre5.setCapacite(2);
        chambre5.setType(ChambreType.DOUBLE);
        chambre5.setEtage("1");

        when(chambreRepository.findAll()).thenReturn(List.of(chambre1, chambre2, chambre3, chambre4, chambre5));
        when(chambreReservationRepository.findByChambre_Id(1L)).thenReturn(List.of());
        when(chambreReservationRepository.findByChambre_Id(2L)).thenReturn(List.of());
        when(chambreReservationRepository.findByChambre_Id(3L)).thenReturn(List.of());
        when(chambreReservationRepository.findByChambre_Id(4L)).thenReturn(List.of(cr));
        when(chambreReservationRepository.findByChambre_Id(5L)).thenReturn(List.of());


        List<Chambre> result = chambreReservationService.findChambresDisponibles(dateDebut, dateFin, capacite, type, etage);


        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getId());
    }
}
