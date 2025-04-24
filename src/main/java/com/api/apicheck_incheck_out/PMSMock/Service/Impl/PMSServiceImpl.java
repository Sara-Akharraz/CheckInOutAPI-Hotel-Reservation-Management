package com.api.apicheck_incheck_out.PMSMock.Service.Impl;

import com.api.apicheck_incheck_out.DTO.ReservationDTO;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.PMSMock.Service.PMSService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class PMSServiceImpl implements PMSService {
    private static final String FILE_PATH ="src/main/resources/pms_mock_data.json";
    private static final ObjectMapper objectMapper=new ObjectMapper().registerModule(new JavaTimeModule());
    private static final Map<Long,ReservationDTO> mockReservations=new HashMap<>();
    private static Long nextId=1L;
    private Long generateNextId(){
        return nextId++;
    }
    @PostConstruct
    public void loadMockData(){
        File file =new File(FILE_PATH);
        if(file.exists()) {
            try {
                Map<Long,ReservationDTO> reservations = objectMapper.readValue(file, new TypeReference<Map<Long, ReservationDTO>>() {
                });
                mockReservations.clear();
                mockReservations.putAll(reservations);
                System.out.println("PMS Mock Data loaded from file");
            }
            catch (IOException e){
                System.err.println("Failed to load PMS Data "+e.getMessage());
            }
        }
    }
    @PreDestroy
    public void saveMockData(){
        try{
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH),mockReservations);
            System.out.println("PMS Mock Data saved to file");
        }catch(IOException e){
            System.err.println("Failed to save PMS Mock data " +e.getMessage());
        }
    }
    @Override
    public ReservationDTO demanderReservation(ReservationDTO reservationDTO) {
        reservationDTO.setId(generateNextId());
        reservationDTO.setStatus(ReservationStatus.En_Attente);
        mockReservations.put(reservationDTO.getId(),reservationDTO);
        saveMockData();
        return reservationDTO;
    }

    @Override
    public ReservationDTO getDemandeReservationById(Long id) {
        return mockReservations.get(id);
    }

    @Override
    public List<ReservationDTO> getAllDemandeReservation() {
        return new ArrayList<>(mockReservations.values());
    }

    @Override
    public ReservationDTO updateDemandeReservation(Long id, ReservationDTO reservationDTO) {
        ReservationDTO reservationDTOExistante=mockReservations.get(id);
        if(reservationDTOExistante != null){
            reservationDTOExistante.setDate_debut(reservationDTO.getDate_debut());
            reservationDTOExistante.setDate_fin(reservationDTO.getDate_fin());
            reservationDTOExistante.setUserId(reservationDTO.getUserId());
            reservationDTOExistante.setChambreList(reservationDTO.getChambreList());
            saveMockData();
            return reservationDTOExistante;
        }

        return null;
    }
}