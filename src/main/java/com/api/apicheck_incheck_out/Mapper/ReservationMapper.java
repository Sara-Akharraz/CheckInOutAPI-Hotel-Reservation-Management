package com.api.apicheck_incheck_out.Mapper;

import com.api.apicheck_incheck_out.Dto.ReservationDTO;
import com.api.apicheck_incheck_out.PMSMock.Model.ChambreModel;
import com.api.apicheck_incheck_out.PMSMock.Service.ChambreService;
import com.api.apicheck_incheck_out.Entity.Facture;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Repository.FactureRepository;
import com.api.apicheck_incheck_out.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationMapper {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FactureRepository factureRepository;
    private ChambreService chambreService;


    public ReservationDTO toDTO(Reservation reservation){
        List<Long> chambresIds = reservation.getChambreList().stream()
                .map(ChambreModel::getId)
                .collect(Collectors.toList());

        List<Long> facturesIds = reservation.getFactureList().stream()
                .map(Facture::getId)
                .collect(Collectors.toList());

        return new ReservationDTO(
                reservation.getId(),
                reservation.getStatus(),
                reservation.getDate_debut(),
                reservation.getDate_fin(),
                reservation.getClient().getId(),
                chambresIds,
                facturesIds
        );
    }
    public Reservation toEntity(ReservationDTO reservationDTO){
        User user=userRepository.findById(reservationDTO.getClientId()).orElseThrow(()->new RuntimeException("User non trouve"));
        List<ChambreModel> chambreList=chambreService.getChambresByReservation(reservationDTO.getId());
        List<Facture> factureList=factureRepository.findAllById(reservationDTO.getFactureList());
        return new Reservation(
                reservationDTO.getId(),
                user,
                chambreList,
                reservationDTO.getDate_debut(),
                reservationDTO.getDate_fin(),
                reservationDTO.getStatus(),
                factureList
        );
    }
}
