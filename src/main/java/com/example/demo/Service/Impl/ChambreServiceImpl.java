package com.example.demo.Service.Impl;

import com.example.demo.Dto.ChambreDto;
import com.example.demo.Entity.Chambre;
import com.example.demo.Entity.User;
import com.example.demo.Enum.ChambreStatut;
import com.example.demo.Mapper.ChambreMapper;
import com.example.demo.Repository.ChambreRepository;
import com.example.demo.Service.ChambreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChambreServiceImpl implements ChambreService {

    @Autowired
    ChambreMapper chambreMapper;

    @Autowired
    ChambreRepository chambreRepository;


    @Override
    public List<ChambreDto> getChambres() {

        List<Chambre> chambres = chambreRepository.findAll();
        if(!chambres.isEmpty())
            return chambres.stream()
                    .map(chambreMapper::toDTO)
                    .collect(Collectors.toList());
        else
            return Collections.emptyList();

    }

    @Override
    public ChambreStatut getChambreStatut(Long id) {
        Chambre c = chambreRepository.findById(id).get();
        return c.getStatut();
    }

    @Override
    public ChambreDto getChambre(Long id) {
        Chambre chambre = chambreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
        return chambreMapper.toDTO(chambre);
    }

    @Override
    public List<ChambreDto> getChambresDisponibles() {
        List<Chambre> chambresDispo = chambreRepository.getChambresDisponibles();
        if(!chambresDispo.isEmpty())
            return chambresDispo.stream()
                    .map(chambreMapper::toDTO)
                    .collect(Collectors.toList());
        else
            return Collections.emptyList();
    }

    @Override
    public void setChambreStatut(Long id, ChambreStatut statut) {
        Chambre chambre = chambreRepository.findById(id).get();
        chambre.setStatut(statut);
        chambreRepository.save(chambre);
    }

    @Override
    public boolean isChambresDisponible(Long id, LocalDate checkInDate, LocalDate checkOutDate) {
        Chambre chambre = chambreRepository.findById(id).get();
        return chambre.getStatut().equals("DISPONIBLE");
    }
}
