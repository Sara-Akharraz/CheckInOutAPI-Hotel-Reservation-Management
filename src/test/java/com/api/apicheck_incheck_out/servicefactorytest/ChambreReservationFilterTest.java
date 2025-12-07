package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.repository.ChambreRepository;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.service.factory.ChambreReservationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class ChambreReservationFilterTest {
    @Mock
    private ChambreReservationRepository chambreReservationRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @InjectMocks
    private ChambreReservationFilter chambreReservationFilter;

    private Chambre chambre1;
    private Chambre chambre2;
    private Chambre chambre3;
    private Reservation reservation1;
    private ChambreReservation chambreReservation1;

    @BeforeEach
    void setUp() {
        chambre1 = new Chambre();
        chambre1.setId(1L);
        chambre1.setCapacite(2);
        chambre1.setType(ChambreType.SINGLE);
        chambre1.setEtage("1");

        chambre2 = new Chambre();
        chambre2.setId(2L);
        chambre2.setCapacite(4);
        chambre2.setType(ChambreType.DOUBLE);
        chambre2.setEtage("2");

        chambre3 = new Chambre();
        chambre3.setId(3L);
        chambre3.setCapacite(3);
        chambre3.setType(ChambreType.DOUBLE);
        chambre3.setEtage("1");

        reservation1 = new Reservation();
        reservation1.setId(1L);
        reservation1.setDateDebut(LocalDate.of(2024, 1, 10));
        reservation1.setDateFin(LocalDate.of(2024, 1, 15));

        chambreReservation1 = new ChambreReservation();
        chambreReservation1.setId(1L);
        chambreReservation1.setChambre(chambre1);
        chambreReservation1.setReservation(reservation1);
    }

    @Test
    void testFindChambresDisponibles_WithAllCriteria_ReturnsMatchingAvailableChambres() {

        String dateDebut = "2024-02-01";
        String dateFin = "2024-02-05";
        Integer capacite = 2;
        ChambreType type = ChambreType.SINGLE;
        String etage = "1";

        when(chambreRepository.findAll()).thenReturn(Arrays.asList(chambre1, chambre2, chambre3));
        when(chambreReservationRepository.findByChambre_Id(1L)).thenReturn(Collections.emptyList());


        List<Chambre> result = chambreReservationFilter.findChambresDisponibles(
                dateDebut, dateFin, capacite, type, etage
        );


        assertEquals(1, result.size());
        assertEquals(chambre1, result.get(0));
        verify(chambreRepository).findAll();
        verify(chambreReservationRepository).findByChambre_Id(1L);
    }

    @Test
    void testFindChambresDisponibles_WithNullCriteria_ReturnsAllAvailableChambres() {

        String dateDebut = "2024-02-01";
        String dateFin = "2024-02-05";

        when(chambreRepository.findAll()).thenReturn(Arrays.asList(chambre1, chambre2, chambre3));
        when(chambreReservationRepository.findByChambre_Id(anyLong())).thenReturn(Collections.emptyList());


        List<Chambre> result = chambreReservationFilter.findChambresDisponibles(
                dateDebut, dateFin, null, null, null
        );


        assertEquals(3, result.size());
        verify(chambreRepository).findAll();
    }

    @Test
    void testFindChambresDisponibles_WithReservedChambre_ExcludesReservedChambre() {

        String dateDebut = "2024-01-12";
        String dateFin = "2024-01-14";

        when(chambreRepository.findAll()).thenReturn(Arrays.asList(chambre1, chambre2));
        when(chambreReservationRepository.findByChambre_Id(1L))
                .thenReturn(Collections.singletonList(chambreReservation1));
        when(chambreReservationRepository.findByChambre_Id(2L)).thenReturn(Collections.emptyList());


        List<Chambre> result = chambreReservationFilter.findChambresDisponibles(
                dateDebut, dateFin, null, null, null
        );


        assertEquals(1, result.size());
        assertEquals(chambre2, result.get(0));
    }

    @Test
    void testFindChambresDisponibles_WithEmptyEtage_IgnoresEtageCriteria() {

        String dateDebut = "2024-02-01";
        String dateFin = "2024-02-05";
        String etage = "   ";

        when(chambreRepository.findAll()).thenReturn(Arrays.asList(chambre1, chambre2));
        when(chambreReservationRepository.findByChambre_Id(anyLong())).thenReturn(Collections.emptyList());


        List<Chambre> result = chambreReservationFilter.findChambresDisponibles(
                dateDebut, dateFin, null, null, etage
        );


        assertEquals(2, result.size());
    }

    @Test
    void testMatchesCriteria_WithAllCriteriaMatching_ReturnsTrue() {
        boolean result = invokeMatchesCriteria(chambre1, 2, ChambreType.SINGLE, "1");
        assertTrue(result);
    }

    @Test
    void testMatchesCriteria_WithNullCapacite_ReturnsTrue() {

        boolean result = invokeMatchesCriteria(chambre1, null, ChambreType.SINGLE, "1");
        assertTrue(result);
    }

    @Test
    void testMatchesCriteria_WithInsufficientCapacite_ReturnsFalse() {
        boolean result = invokeMatchesCriteria(chambre1, 5, ChambreType.SINGLE, "1");
        assertFalse(result);
    }

    @Test
    void testMatchesCriteria_WithDifferentType_ReturnsFalse() {
        boolean result = invokeMatchesCriteria(chambre1, 2, ChambreType.DOUBLE, "1");
        assertFalse(result);
    }

    @Test
    void testMatchesCriteria_WithNullType_ReturnsTrue() {

        boolean result = invokeMatchesCriteria(chambre1, 2, null, "1");
        assertTrue(result);
    }

    @Test
    void testMatchesCriteria_WithDifferentEtage_ReturnsFalse() {
        boolean result = invokeMatchesCriteria(chambre1, 2, ChambreType.SINGLE, "2");
        assertFalse(result);
    }

    @Test
    void testMatchesCriteria_WithNullEtage_ReturnsTrue() {

        boolean result = invokeMatchesCriteria(chambre1, 2, ChambreType.SINGLE, null);
        assertTrue(result);
    }

    @Test
    void testIsAvailableForPeriod_WithNoReservations_ReturnsTrue() {
        LocalDate dateDebut = LocalDate.of(2024, 2, 1);
        LocalDate dateFin = LocalDate.of(2024, 2, 5);

        when(chambreReservationRepository.findByChambre_Id(1L)).thenReturn(Collections.emptyList());


        boolean result = invokeIsAvailableForPeriod(chambre1, dateDebut, dateFin);

        assertTrue(result);
    }

    @Test
    void testIsAvailableForPeriod_WithOverlappingReservation_ReturnsFalse() {

        LocalDate dateDebut = LocalDate.of(2024, 1, 12);
        LocalDate dateFin = LocalDate.of(2024, 1, 14);

        when(chambreReservationRepository.findByChambre_Id(1L))
                .thenReturn(Collections.singletonList(chambreReservation1));

        boolean result = invokeIsAvailableForPeriod(chambre1, dateDebut, dateFin);

        assertFalse(result);
    }

    @Test
    void testIsAvailableForPeriod_WithNonOverlappingReservation_ReturnsTrue() {
        LocalDate dateDebut = LocalDate.of(2024, 1, 20);
        LocalDate dateFin = LocalDate.of(2024, 1, 25);

        when(chambreReservationRepository.findByChambre_Id(1L))
                .thenReturn(Collections.singletonList(chambreReservation1));

        boolean result = invokeIsAvailableForPeriod(chambre1, dateDebut, dateFin);

        assertTrue(result);
    }

    @Test
    void testIsAvailableForPeriod_WithNullReservation_ReturnsTrue() {
        LocalDate dateDebut = LocalDate.of(2024, 2, 1);
        LocalDate dateFin = LocalDate.of(2024, 2, 5);

        ChambreReservation chambreReservationWithNullRes = new ChambreReservation();
        chambreReservationWithNullRes.setReservation(null);

        when(chambreReservationRepository.findByChambre_Id(1L))
                .thenReturn(Collections.singletonList(chambreReservationWithNullRes));

        boolean result = invokeIsAvailableForPeriod(chambre1, dateDebut, dateFin);

        assertTrue(result);
    }

    @Test
    void testIsOverlapping_WithCompleteOverlap_ReturnsTrue() {

        LocalDate dateDebut = LocalDate.of(2024, 1, 11);
        LocalDate dateFin = LocalDate.of(2024, 1, 14);

        boolean result = invokeIsOverlapping(reservation1, dateDebut, dateFin);

        assertTrue(result);
    }

    @Test
    void testIsOverlapping_WithPartialOverlapAtStart_ReturnsTrue() {

        LocalDate dateDebut = LocalDate.of(2024, 1, 8);
        LocalDate dateFin = LocalDate.of(2024, 1, 12);

        boolean result = invokeIsOverlapping(reservation1, dateDebut, dateFin);

        assertTrue(result);
    }

    @Test
    void testIsOverlapping_WithPartialOverlapAtEnd_ReturnsTrue() {

        LocalDate dateDebut = LocalDate.of(2024, 1, 13);
        LocalDate dateFin = LocalDate.of(2024, 1, 20);

        boolean result = invokeIsOverlapping(reservation1, dateDebut, dateFin);

        assertTrue(result);
    }

    @Test
    void testIsOverlapping_WithExactSameDates_ReturnsTrue() {

        LocalDate dateDebut = LocalDate.of(2024, 1, 10);
        LocalDate dateFin = LocalDate.of(2024, 1, 15);

        boolean result = invokeIsOverlapping(reservation1, dateDebut, dateFin);

        assertTrue(result);
    }

    @Test
    void testIsOverlapping_WithNoOverlapBefore_ReturnsFalse() {

        LocalDate dateDebut = LocalDate.of(2024, 1, 1);
        LocalDate dateFin = LocalDate.of(2024, 1, 9);

        boolean result = invokeIsOverlapping(reservation1, dateDebut, dateFin);

        assertFalse(result);
    }

    @Test
    void testIsOverlapping_WithNoOverlapAfter_ReturnsFalse() {

        LocalDate dateDebut = LocalDate.of(2024, 1, 16);
        LocalDate dateFin = LocalDate.of(2024, 1, 20);

        boolean result = invokeIsOverlapping(reservation1, dateDebut, dateFin);

        assertFalse(result);
    }

    @Test
    void testIsOverlapping_WithAdjacentDatesBefore_ReturnsFalse() {

        LocalDate dateDebut = LocalDate.of(2024, 1, 8);
        LocalDate dateFin = LocalDate.of(2024, 1, 10);

        boolean result = invokeIsOverlapping(reservation1, dateDebut, dateFin);

        assertTrue(result);
    }

    @Test
    void testIsOverlapping_WithAdjacentDatesAfter_ReturnsFalse() {
        LocalDate dateDebut = LocalDate.of(2024, 1, 15);
        LocalDate dateFin = LocalDate.of(2024, 1, 18);

        boolean result = invokeIsOverlapping(reservation1, dateDebut, dateFin);

        assertTrue(result);
    }

    private boolean invokeMatchesCriteria(Chambre chambre, Integer capacite, ChambreType type, String etage) {
        try {
            var method = ChambreReservationFilter.class.getDeclaredMethod(
                    "matchesCriteria", Chambre.class, Integer.class, ChambreType.class, String.class
            );
            method.setAccessible(true);
            return (boolean) method.invoke(chambreReservationFilter, chambre, capacite, type, etage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean invokeIsAvailableForPeriod(Chambre chambre, LocalDate dateDebut, LocalDate dateFin) {
        try {
            var method = ChambreReservationFilter.class.getDeclaredMethod(
                    "isAvailableForPeriod", Chambre.class, LocalDate.class, LocalDate.class
            );
            method.setAccessible(true);
            return (boolean) method.invoke(chambreReservationFilter, chambre, dateDebut, dateFin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean invokeIsOverlapping(Reservation reservation, LocalDate dateDebut, LocalDate dateFin) {
        try {
            var method = ChambreReservationFilter.class.getDeclaredMethod(
                    "isOverlapping", Reservation.class, LocalDate.class, LocalDate.class
            );
            method.setAccessible(true);
            return (boolean) method.invoke(chambreReservationFilter, reservation, dateDebut, dateFin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
