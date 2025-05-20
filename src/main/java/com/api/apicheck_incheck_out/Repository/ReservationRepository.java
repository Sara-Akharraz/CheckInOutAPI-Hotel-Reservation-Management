package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

   List<Reservation> findByUserId(Long id);


}
