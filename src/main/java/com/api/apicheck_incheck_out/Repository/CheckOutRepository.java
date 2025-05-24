package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.Check_In;
import com.api.apicheck_incheck_out.Entity.Check_Out;
import com.api.apicheck_incheck_out.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckOutRepository extends JpaRepository<Check_Out, Long> {
    Optional<Check_Out> findByReservation(Reservation reservation);
}
