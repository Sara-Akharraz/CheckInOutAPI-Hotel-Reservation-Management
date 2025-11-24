package com.api.apicheck_incheck_out.mapper;

import com.api.apicheck_incheck_out.dto.ReservationDTO;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.repository.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ReservationMapper {


    private final UserRepository userRepository;

    private final FactureRepository factureRepository;

    private final CheckInRepository checkInRepository;

    private final CheckOutRepository checkOutRepository;

    private final ReservationServiceRepository reservationServiceRepository;

    private final ChambreReservationRepository chambreReservationRepository;

    public ReservationMapper(UserRepository userRepository,FactureRepository factureRepository, CheckInRepository checkInRepository, CheckOutRepository checkOutRepository, ReservationServiceRepository reservationServiceRepository, ChambreReservationRepository chambreReservationRepository) {
        this.userRepository = userRepository;
        this.factureRepository = factureRepository;
        this.checkInRepository = checkInRepository;
        this.checkOutRepository = checkOutRepository;
        this.reservationServiceRepository = reservationServiceRepository;
        this.chambreReservationRepository = chambreReservationRepository;
    }


    public ReservationDTO toDTO(Reservation reservation){
        List<Long> chambresIds = reservation.getChambreReservations().stream()
                .map(ChambreReservation::getId)
                .toList();

        List<Long> facturesIds = reservation.getFactureList().stream()
                .map(Facture::getId)
                .toList();

        List<Long> services =reservation.getServiceList().stream()
                .map(ReservationServices::getId)
                .toList();

        return new ReservationDTO(
                reservation.getId(),
                reservation.getStatus(),
                reservation.getDateDebut(),
                reservation.getDateFin(),
                reservation.getUser().getId(),
                chambresIds,
                reservation.getCheckIn()!=null? reservation.getCheckIn().getId():null,
                reservation.getCheckOut()!=null? reservation.getCheckOut().getId():null,
                facturesIds,
                services
        );
    }
    public Reservation toEntity(ReservationDTO reservationDTO) {
        User user = userRepository.findById(reservationDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + reservationDTO.getUserId()));

        List<ChambreReservation> chambreList = (reservationDTO.getChambreList() != null && !reservationDTO.getChambreList().isEmpty())
                ? chambreReservationRepository.findAllById(reservationDTO.getChambreList())
                : new ArrayList<>();

        List<Facture> factureList = (reservationDTO.getFactureList() != null && !reservationDTO.getFactureList().isEmpty())
                ? factureRepository.findAllById(reservationDTO.getFactureList())
                : new ArrayList<>();

        List<ReservationServices> services=(reservationDTO.getServices()!=null && !reservationDTO.getServices().isEmpty())
                ?reservationServiceRepository.findAllById(reservationDTO.getServices())
                :new ArrayList<>();
        CheckIn checkIn = null;
        if (reservationDTO.getCheckinId() != null) {
            checkIn = checkInRepository.findById(reservationDTO.getCheckinId())
                    .orElseThrow(() -> new RuntimeException("Check-In not found with ID: " + reservationDTO.getCheckinId()));
        }
        CheckOut checkOut = null;
        if (reservationDTO.getCheckoutId() != null) {
            checkOut = checkOutRepository.findById(reservationDTO.getCheckoutId())
                    .orElseThrow(() -> new RuntimeException("Check-Out not found with ID: " + reservationDTO.getCheckoutId()));
        }

        return new Reservation(
                reservationDTO.getId(),
                user,
                chambreList,
                reservationDTO.getDateDebut(),
                reservationDTO.getDateFin(),
                reservationDTO.getStatus(),
                factureList,
                checkIn,
                checkOut,
                services
        );

    }
}
