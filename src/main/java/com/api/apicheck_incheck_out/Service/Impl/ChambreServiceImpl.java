package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enum.ChambreStatut;
import com.api.apicheck_incheck_out.Enums.ChambreType;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.Repository.ChambreRepository;
import com.api.apicheck_incheck_out.Service.ChambreService;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChambreServiceImpl implements ChambreService {

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public Chambre addChambre(Chambre chambre) {
        return chambreRepository.save(chambre);
    }

    @Override
    public Chambre updateChambre(Long id_chambre, Chambre updateChambre) {
        Optional<Chambre> chambre=chambreRepository.findById(id_chambre);
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
            throw new RuntimeException("Chambre non trouvée avec l'id :" +id_chambre);
        }
    }

    @Override
    public void deleteChambre(Long id) {
        if(!chambreRepository.existsById(id)) {
            throw new RuntimeException("Chambre non trouvée avec l'id :"+id);
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
                .orElseThrow(() -> new RuntimeException("Chambre non trouvée avec l'id : " + id));
    }



}