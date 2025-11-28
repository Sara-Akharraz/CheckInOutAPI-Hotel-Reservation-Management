package com.api.apicheck_incheck_out.mappertest;

import com.api.apicheck_incheck_out.dto.CheckOutDTO;
import com.api.apicheck_incheck_out.entity.CheckOut;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.enums.CheckOutStatut;
import com.api.apicheck_incheck_out.mapper.CheckOutMapper;
import com.api.apicheck_incheck_out.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class CheckoutMapperTest {
    @Mock
    private ReservationService reservationService;
    @InjectMocks
    private CheckOutMapper checkoutMapper;

    @Test
    void testToEntity(){
        CheckOutDTO dto=CheckOutDTO.builder()
                .id(1L)
                .checkOutStatut(CheckOutStatut.EN_ATTENTE)
                .idReservation(1L)
                .build();
        Reservation res=new Reservation();
        res.setId(1L);
        when(reservationService.getReservationById(1L)).thenReturn(res);
        CheckOut checkOut=checkoutMapper.toEntity(dto);
        assertNotNull(checkOut);
        assertEquals(1L,checkOut.getId());
        assertEquals(CheckOutStatut.EN_ATTENTE, checkOut.getCheckOutStatut());
        assertNotNull(checkOut.getReservation());
        assertEquals(1L, checkOut.getReservation().getId());

        verify(reservationService, times(1)).getReservationById(1L);
    }
    @Test
    void testToDTO() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);

        CheckOut checkOut = CheckOut.builder()
                .id(2L)
                .checkOutStatut(CheckOutStatut.EN_ATTENTE)
                .reservation(reservation)
                .build();

        CheckOutDTO dto = checkoutMapper.toDTO(checkOut);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals(CheckOutStatut.EN_ATTENTE, dto.getCheckOutStatut());
        assertEquals(1L, dto.getIdReservation());
    }
    @Test
    void testToDTOList(){
        Reservation res1=new Reservation();
        res1.setId(1L);
        Reservation res2=new Reservation();
        res2.setId(2L);

        CheckOut c1 = CheckOut.builder().id(10L).checkOutStatut(CheckOutStatut.CONFIRMEE).reservation(res1).build();
        CheckOut c2 = CheckOut.builder().id(20L).checkOutStatut(CheckOutStatut.EN_ATTENTE).reservation(res2).build();

        List<CheckOut> list= List.of(c1,c2);
        List<CheckOutDTO> result=checkoutMapper.toDTOList(list);

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(1L, result.get(0).getIdReservation());

        assertEquals(20L, result.get(1).getId());
        assertEquals(2L, result.get(1).getIdReservation());
    }

}
