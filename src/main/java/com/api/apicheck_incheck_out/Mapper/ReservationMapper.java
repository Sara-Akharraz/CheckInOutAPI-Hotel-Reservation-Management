package com.api.apicheck_incheck_out.Mapper;

import com.api.apicheck_incheck_out.DTO.ReservationDTO;
import com.api.apicheck_incheck_out.Entity.*;
import com.api.apicheck_incheck_out.Repository.ChambreRespository;
import com.api.apicheck_incheck_out.Repository.CheckInRepository;
import com.api.apicheck_incheck_out.Repository.FactureRepository;
import com.api.apicheck_incheck_out.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationMapper {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChambreRespository chambreRespository;
    @Autowired
    private FactureRepository factureRepository;
    @Autowired
    private CheckInRepository checkInRepository;


    public ReservationDTO toDTO(Reservation reservation){
        List<Long> chambresIds = reservation.getChambreList().stream()
                .map(Chambre::getId)
                .collect(Collectors.toList());

        List<Long> facturesIds = reservation.getFactureList().stream()
                .map(Facture::getId)
                .collect(Collectors.toList());

        return new ReservationDTO(
                reservation.getId(),
                reservation.getStatus(),
                reservation.getDate_debut(),
                reservation.getDate_fin(),
                reservation.getUser().getId(),
                chambresIds,
                reservation.getCheckIn()!=null? reservation.getCheckIn().getId():null,
                facturesIds
        );
    }
    public Reservation toEntity(ReservationDTO reservationDTO){
        User user=userRepository.findById(reservationDTO.getUserId()).orElseThrow(()->new RuntimeException("User non trouve"));
        List<Chambre> chambreList=(reservationDTO.getChambreList()!=null && !reservationDTO.getChambreList().isEmpty())
                ?chambreRespository.findAllById(reservationDTO.getChambreList()):new ArrayList<>();
        List<Facture> factureList=(reservationDTO.getFactureList()!=null && !reservationDTO.getFactureList().isEmpty())
                ?factureRepository.findAllById(reservationDTO.getFactureList()):new ArrayList<>();
        Check_In checkIn=null;
        if(reservationDTO.getCheckinId()!=null){
            checkIn=checkInRepository.findById(reservationDTO.getCheckinId()).orElseThrow(()-> new RuntimeException("Check In not found with ID :" +reservationDTO.getCheckinId()));
        }
        return new Reservation(
                reservationDTO.getId(),
                user,
                chambreList,
                reservationDTO.getDate_debut(),
                reservationDTO.getDate_fin(),
                reservationDTO.getStatus(),
                factureList,
                checkIn
        );
    }
}
