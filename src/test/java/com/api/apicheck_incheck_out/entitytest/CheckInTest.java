package com.api.apicheck_incheck_out.entitytest;

import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.CheckInStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CheckInTest {
    @Test
    void testCheckIn_Equals_HashCode_ToString(){
        CheckIn checkIn1 = CheckIn.builder()
                .id(1L)
                .dateCheckIn(LocalDate.of(2025, 11, 28))
                .status(CheckInStatus.VALIDE)
                .reservation(new Reservation())
                .build();

        CheckIn checkIn2 = CheckIn.builder()
                .id(1L)
                .dateCheckIn(LocalDate.of(2025, 11, 28))
                .status(CheckInStatus.VALIDE)
                .reservation(new Reservation())
                .build();

        CheckIn checkIn3 = CheckIn.builder()
                .id(2L)
                .dateCheckIn(LocalDate.of(2025, 11, 29))
                .status(CheckInStatus.EN_ATTENTE)
                .build();

        assertEquals(checkIn1, checkIn2);
        assertNotEquals(checkIn1, checkIn3);
        assertTrue(checkIn1.equals(checkIn2));

        assertEquals(checkIn1.hashCode(), checkIn2.hashCode());
        assertNotEquals(checkIn1.hashCode(), checkIn3.hashCode());

        String s = checkIn1.toString();
        assertTrue(s.contains("VALIDE") || s.contains("2025"));
    }
}
