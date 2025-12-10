package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.repository.ChambreRepository;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
public class ChambreReservationFilter {
    private final ChambreReservationRepository chambreReservationRepository;
    private final ChambreRepository chambreRepository;

    public ChambreReservationFilter(ChambreReservationRepository chambreReservationRepository, ChambreRepository chambreRepository) {
        this.chambreReservationRepository = chambreReservationRepository;
        this.chambreRepository = chambreRepository;
    }

    public List<Chambre> findChambresDisponibles(
            String dateDebut,
            String dateFin,
            Integer capacite,
            ChambreType type,
            String etage
    ) {
        LocalDate datedebut = LocalDate.parse(dateDebut);
        LocalDate datefin = LocalDate.parse(dateFin);

        List<Chambre> allChambres = chambreRepository.findAll();

        return allChambres.stream()
                .filter(chambre -> matchesCriteria(chambre, capacite, type, etage))
                .filter(chambre -> isAvailableForPeriod(chambre, datedebut, datefin))
                .toList();
    }
    private boolean matchesCriteria(Chambre chambre, Integer capacite, ChambreType type, String etage) {
        return (capacite == null || chambre.getCapacite() >= capacite)
                && (type == null || chambre.getType() == type)
                && (etage == null || etage.trim().isEmpty() || chambre.getEtage().equals(etage));
    }
    private boolean isAvailableForPeriod(Chambre chambre, LocalDate dateDebut, LocalDate dateFin) {
        return chambreReservationRepository
                .findByChambre_Id(chambre.getId())
                .stream()
                .map(ChambreReservation::getReservation)
                .filter(Objects::nonNull)
                .noneMatch(reservation -> isOverlapping(reservation, dateDebut, dateFin));
    }

    private boolean isOverlapping(Reservation reservation, LocalDate dateDebut, LocalDate dateFin) {
        LocalDate resDebut = reservation.getDateDebut();
        LocalDate resFin = reservation.getDateFin();
        return !(dateFin.isBefore(resDebut) || dateDebut.isAfter(resFin));
    }
}
