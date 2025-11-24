package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreNotFoundException;
import com.api.apicheck_incheck_out.repository.ChambreRepository;
import com.api.apicheck_incheck_out.service.ChambreService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChambreServiceImpl implements ChambreService {


    private final ChambreRepository chambreRepository;


    public ChambreServiceImpl(ChambreRepository chambreRepository) {
        this.chambreRepository = chambreRepository;
    }

    @Override
    public Chambre addChambre(Chambre chambre) {
        return chambreRepository.save(chambre);
    }

    @Override
    public Chambre updateChambre(Long idChambre, Chambre updateChambre) {
       Optional<Chambre> chambre=chambreRepository.findById(idChambre);
       if(chambre.isPresent()){
           Chambre existedChambre=chambre.get();
           existedChambre.setPrix(updateChambre.getPrix());
           existedChambre.setNom(updateChambre.getNom());
           existedChambre.setType(updateChambre.getType());
           existedChambre.setEtage(updateChambre.getEtage());
           existedChambre.setCapacite(updateChambre.getCapacite());

           chambreRepository.save(existedChambre);
           return existedChambre;

       }else{
           throw new ChambreNotFoundException("Chambre non trouvée avec l'id :" +idChambre);
       }
    }

    @Override
    public void deleteChambre(Long id) {
        if(!chambreRepository.existsById(id)) {
            throw new ChambreNotFoundException("Chambre non trouvée avec l'id :"+id);
        }
        chambreRepository.deleteById(id);
    }

    @Override
    public List<Chambre> getChambres() {
        return chambreRepository.findAll();
    }



    @Override
    public Chambre getChambre(Long id) {

        return chambreRepository.findById(id)
                .orElseThrow(() -> new ChambreNotFoundException("Chambre non trouvée avec l'id : " + id));
    }



}
