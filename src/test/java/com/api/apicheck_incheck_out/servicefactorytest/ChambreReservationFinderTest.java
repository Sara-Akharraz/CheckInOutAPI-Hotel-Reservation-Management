package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreReservationNotFoundException;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.service.factory.ChambreReservationFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class ChambreReservationFinderTest {
    @Mock
    private ChambreReservationRepository chambreReservationRepository;

    @InjectMocks
    private ChambreReservationFinder finder;

    private ChambreReservation chambreReservation1;
    private ChambreReservation chambreReservation2;
    private Reservation reservation;
    private Chambre chambre;

    @BeforeEach
    void setUp(){
        reservation =new Reservation();
        reservation.setId(1L);

        chambre=new Chambre();
        chambre.setId(10L);

        chambreReservation1 = new ChambreReservation();
        chambreReservation1.setId(100L);
        chambreReservation1.setReservation(reservation);
        chambreReservation1.setChambre(chambre);

        chambreReservation2 = new ChambreReservation();
        chambreReservation2.setId(200L);
        chambreReservation2.setReservation(reservation);
    }
    @Test
    void testFindChambreReservation_WhenExists_ReturnsChambreReservation() {
        Long idReservation = 1L;
        Long idChambre = 10L;

        when(chambreReservationRepository.findByReservation_IdAndChambre_Id(idReservation, idChambre))
                .thenReturn(Optional.of(chambreReservation1));

        ChambreReservation result = finder.findChambreReservation(idReservation, idChambre);

        assertNotNull(result);
        assertEquals(chambreReservation1, result);
        assertEquals(100L, result.getId());
        verify(chambreReservationRepository).findByReservation_IdAndChambre_Id(idReservation, idChambre);
    }
    @Test
    void testFindChambreReservation_WhenNotFound_ThrowsException(){
        Long idReservation=1L;
        Long idChambre=10L;
        when(chambreReservationRepository.findByReservation_IdAndChambre_Id(idReservation, idChambre))
                .thenReturn(Optional.empty());

        ChambreReservationNotFoundException ex=assertThrows(ChambreReservationNotFoundException.class,()->finder.findChambreReservation(idReservation,idChambre));

        assertEquals(   "ChambreReservation non trouvée pour la réservation ID : 1 et chambre ID : 10",
                ex.getMessage());
    }
    @Test
    void testFindChambreReservation_WithNullIds_CallsRepository() {
        Long idReservation = null;
        Long idChambre = null;

        when(chambreReservationRepository.findByReservation_IdAndChambre_Id(idReservation, idChambre))
                .thenReturn(Optional.empty());

        assertThrows(
                ChambreReservationNotFoundException.class,
                () -> finder.findChambreReservation(idReservation, idChambre)
        );

        verify(chambreReservationRepository).findByReservation_IdAndChambre_Id(idReservation, idChambre);
    }
    @Test
    void testFindChambreReservationByReservationId_WhenExists_ReturnsListOfChambreReservations() {
        Long idReservation = 1L;
        List<ChambreReservation> expectedList = List.of(chambreReservation1, chambreReservation2);

        when(chambreReservationRepository.findByReservation_Id(idReservation))
                .thenReturn(expectedList);

        List<ChambreReservation> result = finder.findChambreReservationByReservationId(idReservation);


        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        assertTrue(result.contains(chambreReservation1));
        assertTrue(result.contains(chambreReservation2));
        verify(chambreReservationRepository).findByReservation_Id(idReservation);
    }
    @Test
    void testFindChambreReservationByReservationId_WhenEmpty_ThrowsException() {

        Long idReservation = 10L;

        when(chambreReservationRepository.findByReservation_Id(idReservation))
                .thenReturn(Collections.emptyList());

        ChambreReservationNotFoundException exception = assertThrows(
                ChambreReservationNotFoundException.class,
                () -> finder.findChambreReservationByReservationId(idReservation)
        );

        assertEquals(
                "Aucune ChambreReservation trouvée pour la réservation ID : 10",
                exception.getMessage()
        );
        verify(chambreReservationRepository).findByReservation_Id(idReservation);
    }
    @Test
    void testFindChambreReservationsByReservation_WhenExists_ReturnsListOfChambreReservations() {
        List<ChambreReservation> expectedList = List.of(chambreReservation1, chambreReservation2);

        when(chambreReservationRepository.findByReservation(reservation))
                .thenReturn(expectedList);

        List<ChambreReservation> result = finder.findChambreReservationsByReservation(reservation);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        assertTrue(result.contains(chambreReservation1));
        assertTrue(result.contains(chambreReservation2));
        verify(chambreReservationRepository).findByReservation(reservation);
    }
    @Test
    void testFindChambreReservationsByReservation_WhenEmpty_ReturnsEmptyList() {

        when(chambreReservationRepository.findByReservation(reservation))
                .thenReturn(Collections.emptyList());

        List<ChambreReservation> result = finder.findChambreReservationsByReservation(reservation);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chambreReservationRepository).findByReservation(reservation);
    }
}
