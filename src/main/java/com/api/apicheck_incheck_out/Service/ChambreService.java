package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Enum.ChambreStatut;
import com.api.apicheck_incheck_out.Enums.ChambreType;

import java.util.List;


public interface ChambreService {
    public Chambre addChambre(Chambre chambre);
    public Chambre updateChambre(Long id_chambre,Chambre updateChambre);
    public void deleteChambre(Long id);
    public List<Chambre> getChambres();
    public Chambre getChambre(Long id);

}