package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.service.factory.ReservationConfirmationManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
 class ReservationConfirmationTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ChambreReservationRepository chambreReservationRepository;
    @Mock
    private ReservationServiceRepository reservationServiceRepository;
    @InjectMocks

    private ReservationConfirmationManager manager;

    @Test
    void testConfirmReservation() {

        Reservation reservation = new Reservation();
        reservation.setId(1L);

        ChambreReservation cr1 = new ChambreReservation();
        cr1.setReservation(reservation);
        cr1.setStatut(ChambreStatut.DISPONIBLE);

        ReservationServices rs1 = new ReservationServices();
        rs1.setReservation(reservation);
        rs1.setPaiementStatus(PaiementStatus.EN_ATTENTE);

        reservation.setChambreReservations(List.of(cr1));
        reservation.setServiceList(List.of(rs1));

        manager.confirmReservation(reservation);

        assert(reservation.getStatus() == ReservationStatus.CONFIRMEE);
        assert(cr1.getStatut() == ChambreStatut.OCCUPEE);
        assert(rs1.getPaiementStatus() == PaiementStatus.PAYE);

        verify(reservationRepository, times(1)).save(reservation);
        verify(chambreReservationRepository, times(1)).saveAll(reservation.getChambreReservations());
        verify(reservationServiceRepository, times(1)).saveAll(reservation.getServiceList());
    }
}
