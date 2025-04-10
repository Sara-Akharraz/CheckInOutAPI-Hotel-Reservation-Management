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
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChambreServiceImp implements ChambreService {

    @Autowired
    ReservationService reservationService;

    private final String JSON_FILE_PATH = "/Chambres_mock_data.json";


        private List<ChambreModel> chambres;

        @PostConstruct
        public void loadChambres() {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                InputStream inputStream = getClass().getResourceAsStream(JSON_FILE_PATH);

                // Check if the file is found
                if (inputStream == null) {
                    throw new FileNotFoundException("Could not find the file at " + JSON_FILE_PATH);
                }

                // Deserialize the JSON into the list
                this.chambres = objectMapper.readValue(inputStream, new TypeReference<List<ChambreModel>>() {});
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("JSON file not found: " + JSON_FILE_PATH, e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load Chambres from JSON", e);
            }
        }

        @PreDestroy
        public void saveChambres() {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(JSON_FILE_PATH), this.chambres);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to save Chambres", e);
            }
        }


    @Override
    public List<ChambreModel> getChambres() {
        return chambres;
    }

    @Override
    public ChambreStatut getChambreStatut(Long id) {
        for (ChambreModel chambre : chambres) {
            if(chambre.getId()==id){
                return chambre.getStatut();
            }
        }
        throw new RuntimeException("Chambre does not exist");
    }

    @Override
    public ChambreModel getChambre(Long id) {
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
        for (ChambreModel chambre : chambres) {
            if (chambre.getId() == id) {
                chambre.setStatut(ChambreStatut.valueOf("OCCUPEE"));
                chambre.getId_assignedReservation();
            }
            saveChambres();
        }
    }


@Override
public void setChambreDisponible(Long id) {
    for (ChambreModel chambre : chambres) {
        if (chambre.getId() == id) {
            chambre.setStatut(ChambreStatut.valueOf("DISPONIBLE"));
            chambre.getId_assignedReservation();
        }
        saveChambres();
    }
}
}