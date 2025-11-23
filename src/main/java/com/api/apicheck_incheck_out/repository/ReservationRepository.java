package com.api.apicheck_incheck_out.repository;

import com.api.apicheck_incheck_out.entity.Reservation;
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
           +" cr.dateFin = :dateFin " )
   List<Reservation> findByDateFin(
           @Param("dateFin") LocalDate dateFin);

   @Query("SELECT cr FROM Reservation cr WHERE cr.dateDebut = :dateDebut")
   List<Reservation> findByDateDebut(@Param("dateDebut") LocalDate dateDebut);


}
