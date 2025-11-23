package com.api.apicheck_incheck_out.repository;

import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIn,Long> {
    Optional<CheckIn> findByReservation(Reservation reservation);
}
