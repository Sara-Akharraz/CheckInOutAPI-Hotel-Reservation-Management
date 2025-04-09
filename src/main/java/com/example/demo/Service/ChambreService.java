package com.example.demo.Service;

import com.example.demo.Dto.ChambreDto;
import com.example.demo.Enum.ChambreStatut;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


public interface ChambreService {
    public List<ChambreDto> getChambres();
    public ChambreStatut getChambreStatut(Long id);
    public ChambreDto getChambre(Long id);
    public List<ChambreDto> getChambresDisponibles();
    public void setChambreStatut(Long id, ChambreStatut statut);

    //possible tkoun f reserv !!!
    public boolean isChambresDisponible(Long id, LocalDate checkInDate, LocalDate checkOutDate);
}