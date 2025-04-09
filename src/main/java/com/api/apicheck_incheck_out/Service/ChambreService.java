package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.Dto.ChambreDto;
import com.api.apicheck_incheck_out.Enum.ChambreStatut;

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
