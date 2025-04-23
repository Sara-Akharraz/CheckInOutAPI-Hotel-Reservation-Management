package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.ChambreReservation;
import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import com.api.apicheck_incheck_out.Enums.ChambreType;

import java.util.List;

public interface ChambreReservationService {
    public List<Chambre> getChambresDisponibles();
    public void setChambreOccupee(Long id_reservation);
    public void setChambreDisponible(Long id);
    public void setChambreReserved(Long id,Long id_reservation);
    public List<Chambre> findChambresDisponibles(String dateDebut, String dateFin, Integer capacite, ChambreType type, String etage);
    public ChambreStatut getChambreStatut(Long reservationId, Long chambreId);
    public List<Chambre> getChambresByReservation(Long id);
}
