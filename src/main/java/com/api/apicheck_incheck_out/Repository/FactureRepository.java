package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.Facture;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.FactureType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactureRepository extends JpaRepository<Facture,Long> {
    Facture findByReservationAndType(Reservation reservation, FactureType type);
}
