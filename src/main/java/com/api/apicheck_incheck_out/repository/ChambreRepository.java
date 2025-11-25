package com.api.apicheck_incheck_out.repository;

import com.api.apicheck_incheck_out.entity.Chambre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChambreRepository extends JpaRepository<Chambre,Long> {
    List<Chambre> findByIdIn(List<Long> ids);
}
