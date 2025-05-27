package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

   List<Reservation> findByUserId(Long id);
   @Query("SELECT cr FROM Reservation cr WHERE "
           +" cr.date_fin = :dateFin " )
   List<Reservation> findByDateFin(
           @Param("dateFin") LocalDate dateFin);

   @Query("SELECT cr FROM Reservation cr WHERE "
           +" cr.date_debut = :dateDebut " )
   List<Reservation> findByDateDebut(@Param("dateDebut") LocalDate dateDebut);


}
