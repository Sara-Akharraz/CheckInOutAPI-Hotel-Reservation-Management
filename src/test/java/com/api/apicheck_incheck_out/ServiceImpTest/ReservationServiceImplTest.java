package com.api.apicheck_incheck_out.ServiceImpTest;

import com.api.apicheck_incheck_out.DTO.ReservationDTO;
import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Mapper.ReservationMapper;
import com.api.apicheck_incheck_out.PMSMock.Service.PMSService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        chambres=Arrays.asList(chambre);
        reservation=new Reservation(1L,user,chambres, LocalDate.of(2025,4,4),LocalDate.of(2025,4,25), ReservationStatus.En_Attente,null,null);
    }

    @Test
    public void TestaddReservation(){
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        Reservation createdReservation =reservationService.addReservation(reservation);

        assertNotNull(createdReservation);
        assertEquals(LocalDate.of(2025,4,4),createdReservation.getDate_debut());
    }
    @Test
    public void TestgetAllReservations(){
        List<Reservation> reservations= Arrays.asList(reservation,new Reservation(2L,user,chambres,LocalDate.of(2025,4,10),LocalDate.of(2025,4,18),ReservationStatus.Confirmee,null,null));
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<Reservation> reservationList= reservationService.getAllReservations();
        assertEquals(2,reservationList.size());
        verify(reservationRepository,times(1)).findAll();
    }
    @Test
    public void TestgetReservationById(){
        when(reservationRepository.findById(1L)).thenReturn((Optional.of(reservation)));
        Reservation reservationTrouvee = reservationService.getReservationById(1L);
        assertNotNull(reservationTrouvee);
        assertEquals(LocalDate.of(2025,4,4),reservationTrouvee.getDate_debut());
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
        ReservationDTO reservationDTO= new ReservationDTO(1L, ReservationStatus.En_Attente, LocalDate.now(),LocalDate.now().plusDays(5),10L,List.of(1L,2L),null,null);

        when(pmsService.getDemandeReservationById(1L)).thenReturn(reservationDTO);
        //Aucune Reservation trouvee pour cette id
        when(reservationRepository.existsById(1L)).thenReturn(false);
        Reservation reservation=new Reservation();
        when(reservationMapper.toEntity(reservationDTO)).thenReturn(reservation);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation result=reservationService.addReservationPMS(1L);

        verify(reservationRepository,times(1)).save(any(Reservation.class));
        assertNotNull(result);
    }
}
