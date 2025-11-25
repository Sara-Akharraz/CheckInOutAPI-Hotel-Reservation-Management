package com.api.apicheck_incheck_out.service;

import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import com.api.apicheck_incheck_out.enums.ChambreType;

import java.util.List;

public interface ChambreReservationService {
    public List<Chambre> getChambresDisponibles();
    public void setChambreOccupee(Long idReservation);
    public void setChambreDisponible(Long id);
    public void setChambreReserved(Long id,Long idReservation);
    public List<Chambre> findChambresDisponibles(String dateDebut, String dateFin, Integer capacite, ChambreType type, String etage);
    public ChambreStatut getChambreStatut(Long reservationId, Long chambreId);
    public List<Chambre> getChambresByReservation(Long id);
}
