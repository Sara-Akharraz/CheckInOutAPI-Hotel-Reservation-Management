package com.api.apicheck_incheck_out.repository;

import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface ChambreReservationRepository  extends JpaRepository<ChambreReservation,Long> {
    List<ChambreReservation> findByReservation(Reservation reservation);
    List<ChambreReservation> findByReservation_Id(Long id);
    List<ChambreReservation> findByStatut(ChambreStatut statut);
    Optional<ChambreReservation> findByReservation_IdAndChambre_Id(Long reservationId, Long chambreId);
    List<ChambreReservation> findByChambre_Id(Long id);
    // Trouver des ChambreReservation avec des chambres données et les mêmes dates
    @Query("SELECT cr FROM ChambreReservation cr WHERE cr.chambre.id IN :chambreIds " +
            "AND cr.reservation.dateDebut < :dateFin " +
            "AND cr.reservation.dateFin > :dateDebut")
    List<ChambreReservation> findByChambre_IdInAndReservation_DateDebutAndReservation_DateFin(
            @Param("chambreIds") List<Long> chambreIds,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
}
