package com.api.apicheck_incheck_out.controllertest;


import com.api.apicheck_incheck_out.controller.ReservationController;
import com.api.apicheck_incheck_out.dto.DetailReservationRequestDTO;
import com.api.apicheck_incheck_out.dto.ReservationDTO;
import com.api.apicheck_incheck_out.dto.ReservationRequestDTO;
import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.mapper.ReservationMapper;
import com.api.apicheck_incheck_out.mapper.UserMapper;
import com.api.apicheck_incheck_out.security.JwtService;
import com.api.apicheck_incheck_out.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReservationControllerTest {


    @MockBean
    ReservationService reservationService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    Reservation reservation;
    ReservationDTO reservation1, reservation2;
    @MockBean
    private JwtService jwtService;
    List<Reservation> reservations = new ArrayList<>();
    @MockBean
    ReservationMapper reservationMapper;
    @MockBean
    UserMapper userMapper;

    @BeforeEach
    void setUp(){
         reservation = Reservation.builder()
                .id(1L)
                .status(ReservationStatus.CONFIRMEE)
                .dateDebut(LocalDate.of(2025, 11, 25))
                .dateFin(LocalDate.of(2025, 11, 30))
                .factureList(new ArrayList<>())
                .build();
        reservation1 = ReservationDTO.builder()
                .id(1L)
                .status(ReservationStatus.CONFIRMEE)
                .dateDebut(LocalDate.of(2025, 11, 25))
                .dateFin(LocalDate.of(2025, 11, 30))
                .factureList(new ArrayList<>())
                .build();
        reservation2= ReservationDTO.builder()
                .id(2L)
                .status(ReservationStatus.CONFIRMEE)
                .dateDebut(LocalDate.of(2025, 11, 25))
                .dateFin(LocalDate.of(2025, 11, 30))
                .factureList(new ArrayList<>())
                .build();
    }

    @Test
    void addreservationTest() throws Exception{
        List<Long> chambresIds = new ArrayList<>();
        Chambre chambre = Chambre.builder()
                .id(1L)
                .nom("A5")
                .capacite(2)
                .type(ChambreType.DOUBLE)
                .build();
        chambresIds.add(chambre.getId());
        ReservationRequestDTO request = ReservationRequestDTO.builder()
                .reservationDTO(reservation1)
                .chambresId(chambresIds)
                .build();

        when(reservationMapper.toEntity(any(ReservationDTO.class))).thenReturn(reservation);
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservation1);

        when(reservationService.addReservation(any(Reservation.class),eq(chambresIds))).thenReturn(reservation);

        mockMvc.perform(post("/api/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reservation1.getId()))
                .andExpect(jsonPath("$.status").value(reservation1.getStatus().name()));

        verify(reservationService, times(1)).addReservation(any(Reservation.class), eq(chambresIds));
    }

    @Test
    void getReservationTest() throws Exception{

        when(reservationService.getReservationById(1L)).thenReturn(reservation);
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservation1);
        mockMvc.perform(get("/api/reservation/{id}",reservation.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservation1.getId()))
                .andExpect(jsonPath("$.status").value(reservation1.getStatus().name()));

        verify(reservationService, times(1)).getReservationById(reservation1.getId());
    }

    @Test
    void updateReservationStatusTest() throws Exception{
        reservation.setStatus(ReservationStatus.ANNULEE);
        when(reservationService.updateReservationStatus(1L,ReservationStatus.ANNULEE)).thenReturn(reservation);
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservation1);
        mockMvc.perform(put("/api/reservation/{id}/status",reservation.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ReservationStatus.ANNULEE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservation1.getId()))
                .andExpect(jsonPath("$.status").value(reservation1.getStatus().name()));

        verify(reservationService, times(1)).updateReservationStatus(reservation1.getId(),ReservationStatus.ANNULEE);
    }

    @Test
    void deleteReservationTest() throws Exception {
        doNothing().when(reservationService).deleteReservation(1L);

        mockMvc.perform(delete("/api/reservation/{id}", 1L));

        verify(reservationService, times(1)).deleteReservation(1L);
    }

    @Test
    void searchReservationsTest() throws Exception {
        reservations.add(reservation);
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservation1);

        when(reservationService
                .searchReservations("search", reservation.getDateDebut(), reservation.getDateFin(), reservation.getStatus()))
                .thenReturn(reservations);
        mockMvc.perform(get("/api/reservation/search")
                        .param("search", "search")
                        .param("dateDebut", reservation.getDateDebut().toString())
                        .param("dateFin", reservation.getDateFin().toString())
                        .param("status", reservation.getStatus().name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(reservation.getId()))
                .andExpect(jsonPath("$[0].status").value(reservation.getStatus().name()));
        verify(reservationService, times(1)).searchReservations("search", reservation.getDateDebut(), reservation.getDateFin(), reservation.getStatus());
    }

    @Test
    void getReservationDetailTest() throws Exception {

        DetailReservationRequestDTO detail = DetailReservationRequestDTO.builder()
                .userCin("AB123")
                .userLastName("Alami")
                .build();

        when(reservationService.getReservationDetail(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/reservation/details/{reservationId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userCin").value("AB123"))
                .andExpect(jsonPath("$.userLastName").value("Alami"));

        verify(reservationService, times(1)).getReservationDetail(1L);
    }

    @Test
    void getInfoUserTest() throws Exception {
        User user = User.builder()
                .id(1L)
                .cin("AB1234")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .cin("AB1234")
                .build();

        when(userMapper.toDTO(any(User.class))).thenReturn(userDto);
        when(reservationService.findUserByReservation(1L)).thenReturn(user);

        mockMvc.perform(get("/api/reservation/userinfo/{idReservation}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.cin").value(user.getCin()));

        verify(reservationService, times(1)).findUserByReservation(1L);
    }

    @Test
    void getStatsTest() throws Exception {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalReservations", 5L);
        stats.put("confirmedReservations", 3L);

        when(reservationService.getDashboardStats()).thenReturn(stats);

        mockMvc.perform(get("/api/reservation/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReservations").value(5L))
                .andExpect(jsonPath("$.confirmedReservations").value(3L));

        verify(reservationService, times(1)).getDashboardStats();
    }

    @Test
    void getReservationParUserTest() throws Exception {
        User user = User.builder()
                .id(1L)
                .cin("AB1234")
                .build();
        reservations.add(reservation);

        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservation1);
        when(reservationService.getReservationsByUserId(user.getId())).thenReturn(reservations);

        mockMvc.perform(get("/api/reservation/reservations/user/{userId}",user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(reservation1.getId()))
                .andExpect(jsonPath("$[0].status").value(reservation1.getStatus().name()));

        verify(reservationService, times(1)).getReservationsByUserId(user.getId());
    }
}
