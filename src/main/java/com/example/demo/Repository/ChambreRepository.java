package com.example.demo.Repository;

import com.example.demo.Entity.Chambre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChambreRepository extends JpaRepository<Chambre, Long> {

    @Query("SELECT c from Chambre c where c.statut='DISPONIBLE'")
    List<Chambre> getChambresDisponibles();
}