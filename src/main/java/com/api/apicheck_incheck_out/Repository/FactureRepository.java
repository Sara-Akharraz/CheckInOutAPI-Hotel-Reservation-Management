package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.Facture;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.FactureType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FactureRepository extends JpaRepository<Facture,Long> {
    List<Facture> findAllByReservation_IdAndType(Long reservationId, FactureType type);
    List<Facture> findAllByReservation_Id(Long reservationId);


}
