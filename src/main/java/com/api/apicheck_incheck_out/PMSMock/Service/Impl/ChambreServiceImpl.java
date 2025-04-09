package com.api.apicheck_incheck_out.PMSMock.Service.Impl;

import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enum.ChambreStatut;
import com.api.apicheck_incheck_out.PMSMock.Model.ChambreModel;
import com.api.apicheck_incheck_out.PMSMock.Model.ReservationExtrasModel;
import com.api.apicheck_incheck_out.PMSMock.Service.ChambreService;
import com.api.apicheck_incheck_out.Service.ReservationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ChambreServiceImp implements ChambreService {

    ReservationService reservationService;

    private final String JSON_FILE_PATH = "src/main/resources/Chambres_mock_data.json";

    @PostConstruct
    public List<ChambreModel> loadChambres() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream(JSON_FILE_PATH);
            return objectMapper.readValue(inputStream, new TypeReference<List<ChambreModel>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load Chambres");
        }
    }
    @PreDestroy
    public void saveChambres(List<ChambreModel> chambres) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(JSON_FILE_PATH), chambres);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save Chambres");
        }
    }


    @Override
    public List<ChambreModel> getChambres() {
        return loadChambres();
    }

    @Override
    public ChambreStatut getChambreStatut(Long id) {
        List<ChambreModel> chambres = loadChambres();
        for (ChambreModel chambre : chambres) {
            if(chambre.getId()==id){
                return chambre.getStatut();
            }
        }
        throw new RuntimeException("Chambre does not exist");
    }

    @Override
    public ChambreModel getChambre(Long id) {
        List<ChambreModel> chambres = loadChambres();
        for (ChambreModel chambre : chambres) {
            if(chambre.getId()==id){
                return chambre;
            }
        }
        throw new RuntimeException("Chambre does not exist");
    }

    @Override
    public List<ChambreModel> getChambresByReservation(Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        return reservation.getChambreList();
    }

    @Override
    public List<ChambreModel> getChambresDisponibles() {
        List<ChambreModel> chambresDispo = new ArrayList<>();
        List<ChambreModel> chambres = loadChambres();
        for (ChambreModel chambre : chambres) {
            if(chambre.getStatut().equals("DISPONIBLE")){
                chambresDispo.add(chambre);
            }
        }
        return chambresDispo;
    }

    @Override
    public void setChambreOccupee(Long id, Long id_reservation) {
        Reservation reservation = reservationService.getReservationById(id_reservation);
        List<ChambreModel> chambres = loadChambres();
        for (ChambreModel chambre : chambres) {
            if (chambre.getId() == id) {
                chambre.setStatut(ChambreStatut.valueOf("OCCUPEE"));
                chambre.getId_assignedReservation();
            }
            saveChambres(chambres);
        }
    }


@Override
public void setChambreDisponible(Long id) {
    List<ChambreModel> chambres = loadChambres();
    for (ChambreModel chambre : chambres) {
        if (chambre.getId() == id) {
            chambre.setStatut(ChambreStatut.valueOf("DISPONIBLE"));
            chambre.getId_assignedReservation();
        }
        saveChambres(chambres);
    }
}
}}