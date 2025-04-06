package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactureRepository extends JpaRepository<Facture,Long> {
}
