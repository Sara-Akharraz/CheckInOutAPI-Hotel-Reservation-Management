package com.api.apicheck_incheck_out.entitytest;

import com.api.apicheck_incheck_out.entity.CheckOut;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.CheckOutStatut;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutTest {
     @Test
     void testCheckout(){
         CheckOut checkOut1 = CheckOut.builder()
                 .id(1L)
                 .reservation(new Reservation())
                 .dateCheckOut(LocalDate.of(2025, 11, 28))
                 .checkOutStatut(CheckOutStatut.EN_ATTENTE)
                 .build();

         CheckOut checkOut2 = CheckOut.builder()
                 .id(1L)
                 .reservation(new Reservation())
                 .dateCheckOut(LocalDate.of(2025, 11, 28))
                 .checkOutStatut(CheckOutStatut.EN_ATTENTE)
                 .build();

         CheckOut checkOut3 = CheckOut.builder()
                 .id(3L)
                 .reservation(new Reservation())
                 .dateCheckOut(LocalDate.of(2025, 11, 29))
                 .checkOutStatut(CheckOutStatut.CONFIRMEE)
                 .build();


         assertEquals(checkOut1, checkOut2);
         assertNotEquals(checkOut1, checkOut3);
         assertTrue(checkOut1.equals(checkOut2));

         assertEquals(checkOut1.hashCode(), checkOut2.hashCode());
         assertNotEquals(checkOut1.hashCode(), checkOut3.hashCode());

         String s = checkOut1.toString();
         assertTrue(s.contains("EN_ATTENTE") || s.contains("2025"));


         checkOut1.setId(10L);
         assertEquals(10L, checkOut1.getId());



     }
}
