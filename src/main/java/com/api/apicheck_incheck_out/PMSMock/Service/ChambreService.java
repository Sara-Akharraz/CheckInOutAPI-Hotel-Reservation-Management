package com.api.apicheck_incheck_out.PMSMock.Service;

import com.api.apicheck_incheck_out.Enum.ChambreStatut;
import com.api.apicheck_incheck_out.PMSMock.Model.ChambreModel;

import java.time.LocalDate;
import java.util.List;


public interface ChambreService {
    public List<ChambreModel> getChambres();
    public ChambreStatut getChambreStatut(Long id);
    public ChambreModel getChambre(Long id);
    public List<ChambreModel> getChambresByReservation(Long id);
    public List<ChambreModel> getChambresDisponibles();
    public void setChambreOccupee(Long id, Long id_reservation);
    public void setChambreDisponible(Long id);
}