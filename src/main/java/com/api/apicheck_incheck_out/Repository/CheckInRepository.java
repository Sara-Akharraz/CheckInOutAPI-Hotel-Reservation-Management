package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.Check_In;
import com.api.apicheck_incheck_out.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckInRepository extends JpaRepository<Check_In,Long> {
    Optional<Check_In> findByReservation(Reservation reservation);
}