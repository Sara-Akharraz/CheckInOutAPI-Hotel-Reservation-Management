package com.api.apicheck_incheck_out.PMSMock.Service.Impl;

import com.api.apicheck_incheck_out.PMSMock.Model.ExtraModel;
import com.api.apicheck_incheck_out.PMSMock.Model.ReservationExtrasModel;
import com.api.apicheck_incheck_out.PMSMock.Service.ExtraMock;
import com.api.apicheck_incheck_out.Service.ReservationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExtraMockImpl implements ExtraMock {

    private final String JSON_FILE_PATH = "src/main/resources/Extra_Mock_Data.json";

    @Autowired
    private ReservationService reservationService;

    private List<ReservationExtrasModel> extras = new ArrayList<>();

    @PostConstruct
    public void initExtras() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // Use leading '/' for classpath resources
            InputStream inputStream = getClass().getResourceAsStream(JSON_FILE_PATH);
            if (inputStream != null) {
                extras = objectMapper.readValue(inputStream, new TypeReference<List<ReservationExtrasModel>>() {});
            } else {
                throw new RuntimeException("JSON file not found at: " + JSON_FILE_PATH);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Extras from JSON", e);
        }
    }

    public List<ReservationExtrasModel> loadExtras() {
        return extras;
    }

    @PreDestroy
    public void saveReservations() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File("src/main/resources/Extra_Mock_Data.json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, extras);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save Extras to JSON", e);
        }
    }

    @Override
    public double calculateTotalExtraPrice(Long id_reservation) {
        if (reservationService.getReservationById(id_reservation) != null) {
            // Find the reservation with matching id
            for (ReservationExtrasModel reservationExtra : extras) {
                if (reservationExtra.getId_reservation().equals(id_reservation)) {
                    // Sum up the prices of extras
                    return reservationExtra.getExtras().stream()
                            .mapToDouble(ExtraModel::getPrice)
                            .sum();
                }
            }
            return 0;
        } else {
            throw new RuntimeException("Reservation not Found");
        }
    }

    @Override
    public List<ExtraModel> getExtrasOfReservation(Long id_reservation) {
        if (reservationService.getReservationById(id_reservation) != null) {
            for (ReservationExtrasModel reservationExtra : extras) {
                if (reservationExtra.getId_reservation().equals(id_reservation)) {
                    return reservationExtra.getExtras();
                }
            }
            return new ArrayList<>();
        } else {
            throw new RuntimeException("Reservation not Found");
        }
    }
}
