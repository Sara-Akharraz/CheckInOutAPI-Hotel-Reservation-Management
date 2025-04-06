package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.Chambre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChambreRespository extends JpaRepository<Chambre,Long> {
}
