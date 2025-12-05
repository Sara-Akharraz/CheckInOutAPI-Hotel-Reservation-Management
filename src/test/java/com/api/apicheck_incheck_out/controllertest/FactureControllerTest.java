package com.api.apicheck_incheck_out.controllertest;

import com.api.apicheck_incheck_out.controller.FactureController;
import com.api.apicheck_incheck_out.dto.PaiementRequestDTO;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.FactureType;
import com.api.apicheck_incheck_out.enums.PaiementMethod;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.repository.FactureRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.security.JwtService;
import com.api.apicheck_incheck_out.service.CheckOutService;
import com.api.apicheck_incheck_out.service.FactureService;
import com.api.apicheck_incheck_out.service.ReservationServicesService;
import com.api.apicheck_incheck_out.stripe.service.StripeService;
import com.api.apicheck_incheck_out.stripe.service.impl.StripeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FactureController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FactureControllerTest {
        @MockBean FactureService factureService;
        @MockBean
        StripeServiceImpl stripeService;
        @MockBean
    ReservationServicesService reservationServicesService;
        @MockBean CheckOutService checkOutService;
        @MockBean ReservationRepository reservationRepository;
        @MockBean
        FactureRepository factureRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    CheckOut checkout;
    @MockBean
    private JwtService jwtService;
        Reservation reservation;
        User user;

        @BeforeEach
        void setUp(){
             user = User.builder()
                     .id(1L)
                     .build();
            checkout = CheckOut.builder()
                    .id(1L).build();
             reservation = Reservation.builder()
                    .id(1L)
                     .factureList(new ArrayList<>())
                     .checkOut(checkout)
                     .user(user)
                    .build();
        }
        @Test
    void getMontantCheckInTest() throws Exception{
            when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.ofNullable(reservation));
            when(factureService.calculerMontantCheckIn(reservation)).thenReturn(1800.0);
            mockMvc.perform(get("/api/facture/Montant_checkin")
                            .param("idReservation", reservation.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("1800.0"));

            verify(factureService, times(1)).calculerMontantCheckIn(reservation);
            verify(reservationRepository, times(1)).findById(1L);

        }
        @Test
        void getMontantCheckOutTest() throws Exception{
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.ofNullable(reservation));
        when(checkOutService.getAmount(1L)).thenReturn(1800.0);
        mockMvc.perform(get("/api/facture/Montant_checkOut")
                        .param("idReservation", reservation.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("1800.0"));

        verify(checkOutService, times(1)).getAmount(1L);
            verify(reservationRepository, times(1)).findById(1L);

        }

    @Test
    void payerFactureCheckInTest() throws Exception{

        PaiementRequestDTO paiementRequestDTO = PaiementRequestDTO.builder()
                .reservationId(1L)
                .method(PaiementMethod.STRIPE.name())
                .clientSecret("1234")
                .build();

        when(factureService.payerFactureCheckIn(paiementRequestDTO)).thenReturn(true);
        mockMvc.perform(post("/api/facture//payer_checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paiementRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(factureService, times(1)).payerFactureCheckIn(paiementRequestDTO);
    }

    @Test
    void getAllFacturesTest() throws Exception{

        Facture facture = Facture.builder()
                .id(1L)
                .tax(12.0)
                .build();
        List<Facture> factures = new ArrayList<>();
        factures.add(facture);
        when(reservationRepository.findById(1L)).thenReturn(Optional.ofNullable(reservation));
        when(factureRepository.findAllByReservation_Id(1L)).thenReturn(factures);
        mockMvc.perform(get("/api/facture/factures/{reservationId}/{userId}",1L,1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(factures.get(0).getId()))
                .andExpect(jsonPath("$[0].tax").value(factures.get(0).getTax()));

        verify(reservationRepository, times(1)).findById(1L);
        verify(factureRepository, times(1)).findAllByReservation_Id(1L);
    }

    @Test
    void getAllFactureCheckinTest() throws Exception{

        Facture facture = Facture.builder()
                .id(1L)
                .tax(12.0)
                .build();
        List<Facture> factures = new ArrayList<>();
        factures.add(facture);
        when(reservationRepository.findById(1L)).thenReturn(Optional.ofNullable(reservation));
        when(factureRepository.findAllByReservation_Id(1L)).thenReturn(factures);
        mockMvc.perform(get("/api/facture/factures/{reservationId}",1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(factures.get(0).getId()))
                .andExpect(jsonPath("$[0].tax").value(factures.get(0).getTax()));

        verify(reservationRepository, times(1)).findById(1L);
        verify(factureRepository, times(1)).findAllByReservation_Id(1L);
    }

    @Test
    void createPaymentIntent_validAmount() throws Exception {
        PaymentIntent paymentIntent = new PaymentIntent();
        paymentIntent.setClientSecret("secret_123");

        when(stripeService.createPaymentIntent(100.0)).thenReturn(paymentIntent);

        mockMvc.perform(post("/api/facture/create-intent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientSecret").value("secret_123"));
    }
    @Test
    void createPaymentIntent_amountNull() throws Exception {
        mockMvc.perform(post("/api/facture/create-intent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ }"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Erreur interne : Le montant est requis."));
    }
    @Test
    void createPaymentIntent_amountInvalid() throws Exception {
        mockMvc.perform(post("/api/facture/create-intent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": \"abc\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Erreur interne : Montant invalide."));
    }

    @Test
    void createPaymentIntent_amountNegatif() throws Exception {
        mockMvc.perform(post("/api/facture/create-intent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": -200}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Erreur interne : Le montant doit être supérieur à zéro."));
    }


}
