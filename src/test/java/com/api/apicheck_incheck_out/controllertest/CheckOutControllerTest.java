package com.api.apicheck_incheck_out.controllertest;


import com.api.apicheck_incheck_out.controller.CheckOutController;
import com.api.apicheck_incheck_out.controller.UserController;
import com.api.apicheck_incheck_out.dto.CheckOutDTO;
import com.api.apicheck_incheck_out.entity.CheckOut;
import com.api.apicheck_incheck_out.enums.CheckOutStatut;
import com.api.apicheck_incheck_out.mapper.CheckOutMapper;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.security.JwtService;
import com.api.apicheck_incheck_out.service.CheckOutService;
import com.api.apicheck_incheck_out.service.ReservationService;
import com.api.apicheck_incheck_out.service.ReservationServicesService;
import com.api.apicheck_incheck_out.stripe.StripeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CheckOutController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CheckOutControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CheckOutService checkOutService;

    @MockBean
    private CheckOutMapper checkOutMapper;

    @MockBean ReservationServicesService reservationServicesService;

    @MockBean ReservationRepository reservationRepository;

    @MockBean ReservationService reservationService;

    CheckOut checkOut, checkOut1;
    CheckOutDTO checkOutDTO, checkOutDTO1;
    @BeforeEach
    void setUp(){
        checkOut = CheckOut.builder().id(1L).checkOutStatut(CheckOutStatut.EN_ATTENTE).build();
        checkOut1 = CheckOut.builder().id(2L).checkOutStatut(CheckOutStatut.CONFIRMEE).build();
        checkOutDTO = CheckOutDTO.builder().id(1L).checkOutStatut(CheckOutStatut.EN_ATTENTE).build();
        checkOutDTO1 = CheckOutDTO.builder().id(2L).checkOutStatut(CheckOutStatut.CONFIRMEE).build();
    }

    @Test
    void getAllCheckOutsTest() throws Exception {
        List<CheckOut> checkOutList = List.of(checkOut,checkOut1);
        List<CheckOutDTO> dtoList = List.of(checkOutDTO, checkOutDTO1);
        when(checkOutService.getAllCheckOuts()).thenReturn(checkOutList);
        when(checkOutMapper.toDTOList(checkOutList)).thenReturn(dtoList);

        mockMvc.perform(get("/api/checkout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Liste des check-outs récupérée."))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[1].id").value(2));

        verify(checkOutService, times(1)).getAllCheckOuts();
        verify(checkOutMapper, times(1)).toDTOList(checkOutList);
    }

    @Test
    void getCheckOuts_shouldReturnBadRequest() throws Exception {
        when(checkOutService.getAllCheckOuts()).thenThrow(new RuntimeException("Erreur test"));

        mockMvc.perform(get("/api/checkout"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Erreur lors de la récupération des check-outs: Erreur test"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(checkOutService, times(1)).getAllCheckOuts();
        verifyNoInteractions(checkOutMapper);
    }

    @Test
    void getCheckOutTest() throws Exception {

        when(checkOutService.getCheckOutById(1L)).thenReturn(checkOut);
        when(checkOutMapper.toDTO(checkOut)).thenReturn(checkOutDTO);

        mockMvc.perform(get("/api/checkout/{idCheckOut}",1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("CheckOut trouvé"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(checkOutService, times(1)).getCheckOutById(1L);
        verify(checkOutMapper, times(1)).toDTO(checkOut);
    }

    @Test
    void getCheckOut_shouldReturnBadRequest() throws Exception {
        when(checkOutService.getCheckOutById(1L)).thenThrow(new RuntimeException("Erreur test"));

        mockMvc.perform(get("/api/checkout/{idCheckOut}",1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Erreur test"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(checkOutService, times(1)).getCheckOutById(1L);
        verifyNoInteractions(checkOutMapper);
    }

    @Test
    void addCheckOutTest() throws Exception {
        when(checkOutMapper.toDTO(checkOut)).thenReturn(checkOutDTO);
        when(checkOutMapper.toEntity(checkOutDTO)).thenReturn(checkOut);

        when(checkOutService.addCheckOut(checkOut)).thenReturn(checkOut);

        mockMvc.perform(post("/api/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkOutDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(checkOutDTO.getId()))
                .andExpect(jsonPath("$.data.checkOutStatut").value(checkOutDTO.getCheckOutStatut().name()));

        verify(checkOutService, times(1)).addCheckOut(checkOut);
        verify(checkOutMapper, times(1)).toDTO(checkOut);
        verify(checkOutMapper, times(1)).toEntity(checkOutDTO);
    }

    @Test
    void addCheckOut_shouldReturnBadRequest() throws Exception {
        when(checkOutMapper.toDTO(any(CheckOut.class))).thenReturn(checkOutDTO);
        when(checkOutMapper.toEntity(any(CheckOutDTO.class))).thenReturn(checkOut);

        when(checkOutService.addCheckOut(any(CheckOut.class))).thenThrow(new RuntimeException("Erreur test"));

        mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkOutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Erreur test"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(checkOutService, times(1)).addCheckOut(any(CheckOut.class));
    }

    @Test
    void setCheckOutStatusTest() throws Exception {
        when(checkOutMapper.toDTO(checkOut)).thenReturn(checkOutDTO);

        when(checkOutService.setCheckOutStatus(1L,CheckOutStatut.CONFIRMEE)).thenReturn(checkOut);
        checkOut.setCheckOutStatut(CheckOutStatut.CONFIRMEE);
        mockMvc.perform(put("/api/checkout/{id}/{status}",1L,CheckOutStatut.CONFIRMEE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(checkOutDTO.getId()))
                .andExpect(jsonPath("$.data.checkOutStatut").value(checkOutDTO.getCheckOutStatut().name()));

        verify(checkOutService, times(1)).setCheckOutStatus(1L,CheckOutStatut.CONFIRMEE);
        verify(checkOutMapper, times(1)).toDTO(checkOut);
    }

    @Test
    void getAmountTest() throws Exception {
        when(checkOutService.getAmount(1L)).thenReturn(180.00);
        mockMvc.perform(get("/api/checkout/amount/{id}",1L))
                .andExpect(status().isOk())
                .andExpect(content().string("180.0"));

        verify(checkOutService, times(1)).getAmount(1L);
    }
    @Test
    void payerTest() throws Exception {
        StripeResponse stripeResponse = StripeResponse.builder()
                .sessionUrl("url")
                .sessionId("1234")
                .status("SUCCESS")
                .build();
        when(checkOutService.payer(1L)).thenReturn(stripeResponse);
        mockMvc.perform(get("/api/checkout/payer/{id_checkout}",1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionUrl").value(stripeResponse.getSessionUrl()))
                .andExpect(jsonPath("$.sessionId").value(stripeResponse.getSessionId()));

        verify(checkOutService, times(1)).payer(1L);
    }

    @Test
    void handlePaymentSuccessTest() throws Exception {

        doNothing().when(checkOutService).handlePaymentSuccess(1L);

        mockMvc.perform(post("/api/checkout/validate-payment/{id_checkout}",1L))
                .andExpect(status().isOk());

        verify(checkOutService, times(1)).handlePaymentSuccess(1L);
    }

    @Test
    void handlePaymentSuccessTest_shouldThrowException() throws Exception {
        doThrow(new RuntimeException("Erreur test")).when(checkOutService).handlePaymentSuccess(1L);

        mockMvc.perform(post("/api/checkout/validate-payment/{id_checkout}", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkOutForTodayTest() throws Exception {
        LocalDate date = LocalDate.of(2025,11,25);
        List<CheckOut> checkOutList = List.of(checkOut,checkOut1);
        List<CheckOutDTO> dtoList = List.of(checkOutDTO, checkOutDTO1);
        when(checkOutService.checkoutsForToday(date)).thenReturn(checkOutList);
        when(checkOutMapper.toDTO(any(CheckOut.class))).thenReturn(checkOutDTO);

        mockMvc.perform(get("/api/checkout/today-checkouts")
                .param("date", String.valueOf(date)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(checkOutDTO.getId()))
                .andExpect(jsonPath("$[1].id").value(checkOutDTO.getId()));

        verify(checkOutService, times(1)).checkoutsForToday(date);
        verify(checkOutMapper, times(2)).toDTO(any(CheckOut.class));
    }

    @Test
    void getCheckOutByReservation_found() throws Exception {
        Long reservationId = 1L;

        when(checkOutService.getCheckOutByReservation(reservationId)).thenReturn(checkOut);
        when(checkOutMapper.toDTO(checkOut)).thenReturn(checkOutDTO);

        mockMvc.perform(get("/api/checkout/reservation/{idReservation}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Check-out trouvé."))
                .andExpect(jsonPath("$.data.id").value(checkOutDTO.getId()));

        verify(checkOutService).getCheckOutByReservation(reservationId);
        verify(checkOutMapper).toDTO(checkOut);
    }

    @Test
    void getCheckOutByReservation_notYetDone() throws Exception {
        Long reservationId = 1L;

        when(checkOutService.getCheckOutByReservation(reservationId)).thenReturn(null);
        when(reservationService.existsById(reservationId)).thenReturn(true);

        mockMvc.perform(get("/api/checkout/reservation/{idReservation}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Check-out non encore effectué."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(checkOutService).getCheckOutByReservation(reservationId);
        verify(reservationService).existsById(reservationId);
    }

    @Test
    void getCheckOutByReservation_reservationNotFound() throws Exception {
        Long reservationId = 1L;

        when(checkOutService.getCheckOutByReservation(reservationId)).thenReturn(null);
        when(reservationService.existsById(reservationId)).thenReturn(false);

        mockMvc.perform(get("/api/checkout/reservation/{idReservation}", reservationId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Réservation introuvable."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService).existsById(1L);
        verify(checkOutService).getCheckOutByReservation(reservationId);
        verify(reservationService).existsById(reservationId);
    }
}
