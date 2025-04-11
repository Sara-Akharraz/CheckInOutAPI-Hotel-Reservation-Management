package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    @Query("SELECT r FROM Reservation r JOIN r.chambreList c WHERE r.user.id = :userId " +
            "AND c.id IN :chambreIds " +
            "AND r.date_debut < :dateFin " +
            "AND r.date_fin > :dateDebut")
    List<Reservation> findExistingReservation(@Param("userId") Long userId,
                                              @Param("chambreIds") List<Long> chambreIds,
                                              @Param("dateDebut") LocalDate dateDebut,
                                              @Param("dateFin") LocalDate dateFin);

}
