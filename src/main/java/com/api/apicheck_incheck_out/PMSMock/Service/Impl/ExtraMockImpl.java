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


    private final String JSON_FILE_PATH = "/Extra_Mock_Data.json";
    @Autowired
    private ReservationService reservationService;
    private List<ReservationExtrasModel> extras;

    @PostConstruct
    public void loadExtas() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream(JSON_FILE_PATH);
            if (inputStream == null) {
                throw new RuntimeException("JSON file not found: " + JSON_FILE_PATH);
            }
            extras = objectMapper.readValue(inputStream, new TypeReference<List<ReservationExtrasModel>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load Extras");
        }
    }

    @PreDestroy
    public void saveReservations() {
        try {
            if (extras == null || extras.isEmpty()) {
                System.out.println("No extras to save.");
                return;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(getClass().getResource(JSON_FILE_PATH).toURI());  // Convert URL to File
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, extras);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save Extras");
        }
    }

    @Override
    public double calculateTotalExtraPrice(Long id_reservation) {
        if (reservationService.getReservationById(id_reservation) != null) {
            ReservationExtrasModel reservation = null;
            for (ReservationExtrasModel reservationExtra : extras) {
                if (reservationExtra.getId_reservation() == id_reservation) {
                    reservation = reservationExtra;
                }
            }
            if (reservation != null) {
                double totalPrice = 0;
                for (ExtraModel extra : reservation.getExtras()) {
                    totalPrice += extra.getPrice();
                    break;
                }
                return totalPrice;
            } else {
                return 0;
            }
        }else{
            throw new RuntimeException("Reservation not Found");
        }
    }

    @Override
    public List<ExtraModel> getExtrasOfReservation(Long id_reservation) {
        if (reservationService.getReservationById(id_reservation) != null) {
            ReservationExtrasModel reservation = null;
            for (ReservationExtrasModel reservationExtra : extras) {
                if (reservationExtra.getId_reservation() == id_reservation) {
                    reservation = reservationExtra;
                    break;
                }
            }
            if (reservation != null) {
                return reservation.getExtras();
            } else {
                return new ArrayList<ExtraModel>();
            }

        } else {
            throw new RuntimeException("Reservation not Found");
        }
    }
}
