package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Entity.ReservationServices;
import com.api.apicheck_incheck_out.Entity.Services;
import com.api.apicheck_incheck_out.Enums.PhaseAjoutService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReservationServiceRepository extends JpaRepository<ReservationServices,Long> {
    List<ReservationServices> findByReservationId(Long idReservation);
    List<ReservationServices> findByReservationAndService(Reservation reservation, Services service);

    @Query("SELECT rs FROM ReservationServices rs WHERE rs.reservation.id = :reservationId AND rs.phaseAjoutService = :phase")
    List<ReservationServices> findByReservationAndPhase(
            @Param("reservationId") Long reservationId,
            @Param("phase") PhaseAjoutService phase
    );
    @Query("SELECT rs FROM ReservationServices rs " +
            "WHERE rs.reservation.id = :reservationId " +
            "AND rs.phaseAjoutService = 'sejour' " +
            "AND rs.paiementStatus != 'paye'")
    List<ReservationServices> getRsrvServicesSejourUnpaid(@Param("reservationId") Long reservationId);
}
