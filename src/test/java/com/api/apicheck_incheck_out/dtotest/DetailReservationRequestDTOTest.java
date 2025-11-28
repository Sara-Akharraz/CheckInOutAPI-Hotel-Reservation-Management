package com.api.apicheck_incheck_out.dtotest;

import com.api.apicheck_incheck_out.dto.ChambreDTO;
import com.api.apicheck_incheck_out.dto.DetailReservationRequestDTO;
import com.api.apicheck_incheck_out.dto.ReservationDTO;
import com.api.apicheck_incheck_out.dto.ReservationServiceRequestDTO;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 class DetailReservationRequestDTOTest {
    @Test
    void testDetailReservationRequestDTO() {
        ReservationDTO reservationDTO = ReservationDTO.builder()
                .id(1L)
                .status(ReservationStatus.EN_ATTENTE)
                .dateDebut(LocalDate.of(2025, 11, 28))
                .dateFin(LocalDate.of(2025, 12, 9))
                .userId(10L)
                .chambreList(List.of(1L, 2L))
                .checkinId(null)
                .checkoutId(null)
                .factureList(List.of())
                .services(List.of())
                .build();

        ReservationServiceRequestDTO serviceRequest = new ReservationServiceRequestDTO(
                1L,
                100L,
                "Service1",
                "Description",
                50.0,
                PaiementStatus.EN_ATTENTE
        );

        DetailReservationRequestDTO dto1 = new DetailReservationRequestDTO();
        dto1.setReservationDTO(reservationDTO);
        dto1.setReservationServiceRequestDTO(List.of(serviceRequest));
        dto1.setChambreList(List.of(new ChambreDTO(1L, "Ch1", "1", 100.0, ChambreType.SINGLE, 2, List.of(1L))));
        dto1.setUserFirstName("Sara");
        dto1.setUserLastName("Akharraz");
        dto1.setUserCin("CIN123");
        dto1.setUserPhone("0600000000");

        // getters
        assertEquals("Sara", dto1.getUserFirstName());
        assertEquals("Akharraz", dto1.getUserLastName());
        assertEquals("CIN123", dto1.getUserCin());
        assertEquals("0600000000", dto1.getUserPhone());
        assertNotNull(dto1.getReservationDTO());
        assertNotNull(dto1.getReservationServiceRequestDTO());
        assertNotNull(dto1.getChambreList());

        //toString
        assertNotNull(dto1.toString());

        //equals & hashCode
        DetailReservationRequestDTO dto2 = new DetailReservationRequestDTO(
                reservationDTO,
                List.of(serviceRequest),
                List.of(new ChambreDTO(1L, "Ch1", "1", 100.0, ChambreType.SINGLE, 2, List.of(1L))),
                "Sara", "Akharraz", "CIN123", "0600000000"
        );
        DetailReservationRequestDTO dto3 = new DetailReservationRequestDTO(
                reservationDTO,
                List.of(serviceRequest),
                List.of(new ChambreDTO(2L, "Ch2", "2", 200.0, ChambreType.DOUBLE, 4, List.of(2L))),
                "John", "Doe", "CIN456", "0611111111"
        );

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());

    }
}
