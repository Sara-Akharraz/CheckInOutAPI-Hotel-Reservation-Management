package com.api.apicheck_incheck_out.ServiceImpTest;

import com.api.apicheck_incheck_out.DTO.ReservationDTO;
import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.ChambreReservation;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import com.api.apicheck_incheck_out.Mapper.ReservationMapper;
import com.api.apicheck_incheck_out.PMSMock.Service.PMSService;
import com.api.apicheck_incheck_out.Repository.*;
import com.api.apicheck_incheck_out.Service.Impl.EmailSenderService;
import com.api.apicheck_incheck_out.Service.Impl.ReservationServiceImpl;
import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.Service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {
    @InjectMocks
    private ReservationServiceImpl reservationService;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private PMSService pmsService;
    @Mock
    private ReservationMapper reservationMapper;
    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private EmailSenderService emailSenderService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private  NotificationRepository notificationRepository;
    @Mock
    private ReservationServiceRepository reservationServiceRepository;
    @Mock
    private ServiceRepository serviceRepository;
    @Mock
    private ChambreReservationRepository chambreReservationRepository;



    private Reservation reservation;
    private User user;
    private ChambreReservation chambre;
    List<ChambreReservation> chambres;
    @BeforeEach
    public void setup(){
        user=new User();
        chambre=new ChambreReservation();
        chambre.setId(1L);
        chambres=Arrays.asList(chambre);
        reservation=new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.En_Attente);
        reservation.setUser(user);
        reservation.setChambreReservations(chambres);
        reservation.setDate_debut(LocalDate.of(2025,4,4));
        reservation.setDate_fin(LocalDate.of(2025,4,25));
    }

//    @Test
//    public void TestaddReservation() {
//
//        User mockUser = new User();
//        mockUser.setId(1L);
//
//        ChambreReservation chambresId = new ChambreReservation();
//        chambresId.setId(1L);
//
//        Reservation reservation = new Reservation();
//        reservation.setUser(mockUser);
//        reservation.setDate_debut(LocalDate.of(2025, 4, 4));
//        reservation.setDate_fin(LocalDate.of(2025, 4, 6));
//        reservation.setChambreReservations(List.of(chambresId));
//
//        ChambreReservation chambreFromDb = new ChambreReservation();
//        chambreFromDb.setId(1L);
//        chambreFromDb.setStatut(ChambreStatut.DISPONIBLE);
//
//        when(reservationRepository.findExistingReservation(
//                eq(1L),
//                eq(List.of(1L)),
//                any(LocalDate.class),
//                any(LocalDate.class)
//        )).thenReturn(List.of());
//
//        when(chambreReservationRepository.findById(1L)).thenReturn(Optional.of(chambreFromDb));
//        when(chambreReservationRepository.save(any(ChambreReservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Reservation createdReservation = reservationService.addReservation(reservation);
//
//        assertNotNull(createdReservation);
//        assertEquals(LocalDate.of(2025, 4, 4), createdReservation.getDate_debut());
//        assertEquals(1, createdReservation.getChambreReservations().size());
//
//        verify(reservationRepository, times(1)).save(any(Reservation.class));
//        verify(chambreReservationRepository, times(1)).findById(1L);
//        verify(chambreReservationRepository, times(1)).save(any(ChambreReservation.class));
//    }

    @Test
    public void TestgetAllReservations(){

        Reservation newreservation=new Reservation();
        newreservation.setId(2L);
        newreservation.setUser(user);
        newreservation.setChambreReservations(chambres);
        newreservation.setDate_debut(LocalDate.of(2025,4,10));
        newreservation.setDate_fin(LocalDate.of(2025,4,18));
        newreservation.setStatus(ReservationStatus.Confirmee);

        List<Reservation> reservations= Arrays.asList(reservation, newreservation);
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<Reservation> reservationList= reservationService.getAllReservations();
        assertNotNull(reservationList);
        assertEquals(2,reservationList.size());
        verify(reservationRepository,times(1)).findAll();


    }
    @Test
    public void TestgetReservationById(){
        when(reservationRepository.findById(1L)).thenReturn((Optional.of(reservation)));
        Reservation reservationTrouvee = reservationService.getReservationById(1L);
        assertNotNull(reservationTrouvee);
        assertEquals(LocalDate.of(2025,4,4),reservationTrouvee.getDate_debut());
        when(reservationRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reservationService.getReservationById(2L));
    }
    @Test
    public void TestdeleteReservation(){
        when(reservationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reservationRepository).deleteById(1L);
        reservationService.deleteReservation(1L);
        verify(reservationRepository,times(1)).deleteById(1L);

    }
    @Test
    public void TestupdateReservationStatus(){
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        reservationService.updateReservationStatus(1L,ReservationStatus.Annulee);
        assertEquals(ReservationStatus.Annulee,reservation.getStatus());
        verify(reservationRepository,times(1)).save(any(Reservation.class));
    }
//    @Test
//    public void TestaddReservationPMS(){
//        ReservationDTO reservationDTO= new ReservationDTO();
//        reservationDTO.setId(1L);
//        reservationDTO.setStatus(ReservationStatus.En_Attente);
//        reservationDTO.setDate_debut(LocalDate.now());
//        reservationDTO.setDate_fin(LocalDate.now().plusDays(5));
//        reservationDTO.setUserId(10L);
//        reservationDTO.setChambreList(List.of(1L,2L));
//
//        when(pmsService.getDemandeReservationById(1L)).thenReturn(reservationDTO);
//        //Aucune Reservation trouvee pour cet id
//        when(reservationRepository.existsById(1L)).thenReturn(false);
//        Reservation reservation=new Reservation();
//        when(reservationMapper.toEntity(reservationDTO)).thenReturn(reservation);
//
//        ChambreReservation chambre1=new ChambreReservation();
//        chambre1.setId(1L);
//        chambre1.setStatut(ChambreStatut.DISPONIBLE);
//        ChambreReservation chambre2=new ChambreReservation();
//        chambre2.setId(2L);
//        chambre2.setStatut(ChambreStatut.DISPONIBLE);
//
//        when(chambreReservationRepository.findById(1L)).thenReturn((Optional.of(chambre1)));
//        when(chambreReservationRepository.findById(2L)).thenReturn(Optional.of(chambre2));
//
//
//
//        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
//
//        Reservation result=reservationService.addReservationPMS(1L);
//
//        verify(reservationRepository,times(2)).save(any(Reservation.class));
//        assertNotNull(result);
//    }
}
