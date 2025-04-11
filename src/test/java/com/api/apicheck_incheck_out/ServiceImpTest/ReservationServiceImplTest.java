package com.api.apicheck_incheck_out.ServiceImpTest;

import com.api.apicheck_incheck_out.DTO.ReservationDTO;
import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import com.api.apicheck_incheck_out.Mapper.ReservationMapper;
import com.api.apicheck_incheck_out.PMSMock.Service.PMSService;
import com.api.apicheck_incheck_out.Repository.ChambreRepository;
import com.api.apicheck_incheck_out.Service.Impl.ReservationServiceImpl;
import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
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
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private PMSService pmsService;
    @Mock
    private ReservationMapper reservationMapper;
    @Mock
    private ChambreRepository chambreRepository;
    @InjectMocks
    private ReservationServiceImpl reservationService;



    private Reservation reservation;
    private User user;
    private Chambre chambre;
    List<Chambre> chambres;
    @BeforeEach
    public void setup(){
        user=new User();
        chambre=new Chambre();
        chambre.setId(1L);
        chambres=Arrays.asList(chambre);
        reservation=new Reservation(
                1L,
                user,
                chambres,
                LocalDate.of(2025,4,4),
                LocalDate.of(2025,4,25),
                ReservationStatus.En_Attente,
                null,
                null,
                null);
    }

    @Test
    public void TestaddReservation() {

        User mockUser = new User();
        mockUser.setId(1L);

        Chambre chambresId = new Chambre();
        chambresId.setId(1L);

        Reservation reservation = new Reservation();
        reservation.setUser(mockUser);
        reservation.setDate_debut(LocalDate.of(2025, 4, 4));
        reservation.setDate_fin(LocalDate.of(2025, 4, 6));
        reservation.setChambreList(List.of(chambresId));

        Chambre chambreFromDb = new Chambre();
        chambreFromDb.setId(1L);
        chambreFromDb.setStatut(ChambreStatut.DISPONIBLE);

        when(reservationRepository.findExistingReservation(
                eq(1L),
                eq(List.of(1L)),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(List.of());

        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambreFromDb));
        when(chambreRepository.save(any(Chambre.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation createdReservation = reservationService.addReservation(reservation);

        assertNotNull(createdReservation);
        assertEquals(LocalDate.of(2025, 4, 4), createdReservation.getDate_debut());
        assertEquals(1, createdReservation.getChambreList().size());

        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(chambreRepository, times(1)).findById(1L);
        verify(chambreRepository, times(1)).save(any(Chambre.class));
    }

    @Test
    public void TestgetAllReservations(){
        List<Reservation> reservations= Arrays.asList(reservation,new Reservation(2L,user,chambres,LocalDate.of(2025,4,10),LocalDate.of(2025,4,18),ReservationStatus.Confirmee,null,null,null));
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
    @Test
    public void TestaddReservationPMS(){
        ReservationDTO reservationDTO= new ReservationDTO(
                1L,
                ReservationStatus.En_Attente,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                10L,
                List.of(1L,2L),
                null,
                null,
                null);

        when(pmsService.getDemandeReservationById(1L)).thenReturn(reservationDTO);
        //Aucune Reservation trouvee pour cet id
        when(reservationRepository.existsById(1L)).thenReturn(false);
        Reservation reservation=new Reservation();
        when(reservationMapper.toEntity(reservationDTO)).thenReturn(reservation);

        Chambre chambre1=new Chambre();
        chambre1.setId(1L);
        chambre1.setStatut(ChambreStatut.DISPONIBLE);
        Chambre chambre2=new Chambre();
        chambre2.setId(2L);
        chambre2.setStatut(ChambreStatut.DISPONIBLE);

        when(chambreRepository.findById(1L)).thenReturn((Optional.of(chambre1)));
        when(chambreRepository.findById(2L)).thenReturn(Optional.of(chambre2));



        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation result=reservationService.addReservationPMS(1L);

        verify(reservationRepository,times(2)).save(any(Reservation.class));
        assertNotNull(result);
    }
}
