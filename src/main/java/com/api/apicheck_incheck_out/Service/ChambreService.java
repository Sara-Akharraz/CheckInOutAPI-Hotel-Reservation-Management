package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Enums.ChambreStatut;

import java.util.List;


public interface ChambreService {
    public Chambre addChambre(Chambre chambre);
    public Chambre updateChambre(Long id_chambre,Chambre updateChambre);
    public void deleteChambre(Long id);
    public List<Chambre> getChambres();
    public ChambreStatut getChambreStatut(Long id);
    public Chambre getChambre(Long id);
    public List<Chambre> getChambresByReservation(Long id);
    public List<Chambre> getChambresDisponibles();
    public void setChambreOccupee(Long id, Long id_reservation);
    public void setChambreDisponible(Long id);
    public void setChambreReserved(Long id,Long id_reservation);

}