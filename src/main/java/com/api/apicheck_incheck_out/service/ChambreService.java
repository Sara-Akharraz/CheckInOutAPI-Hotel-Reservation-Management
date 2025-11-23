package com.api.apicheck_incheck_out.service;

import com.api.apicheck_incheck_out.entity.Chambre;

import java.util.List;


public interface ChambreService {
    public Chambre addChambre(Chambre chambre);
    public Chambre updateChambre(Long idChambre,Chambre updateChambre);
    public void deleteChambre(Long id);
    public List<Chambre> getChambres();
    public Chambre getChambre(Long id);

}