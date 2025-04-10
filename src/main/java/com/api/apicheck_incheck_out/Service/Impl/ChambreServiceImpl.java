package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Reservation; // Import Reservation entity
import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import com.api.apicheck_incheck_out.Repository.ChambreRepository;
import com.api.apicheck_incheck_out.Service.ChambreService;
import com.api.apicheck_incheck_out.Repository.ReservationRepository; // Import ReservationRepository for fetching Reservation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChambreServiceImpl implements ChambreService {

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public List<Chambre> getChambres() {
        return chambreRepository.findAll();
    }

    @Override
    public ChambreStatut getChambreStatut(Long id) {
        Chambre chambre = chambreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chambre non trouvée avec l'id : " + id));
        return chambre.getStatut();
    }

    @Override
    public Chambre getChambre(Long id) {
        // Retrieve a specific chambre by its id
        return chambreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chambre non trouvée avec l'id : " + id));
    }

    @Override
    public List<Chambre> getChambresByReservation(Long id) {
        // Fetch chambres associated with a reservation entity
        return chambreRepository.findByReservation_Id(id);
    }

    @Override
    public List<Chambre> getChambresDisponibles() {
        return chambreRepository.findByStatut(ChambreStatut.DISPONIBLE);
    }

    @Override
    public void setChambreOccupee(Long id, Long id_reservation) {

        Chambre chambre = chambreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chambre non trouvée avec l'id : " + id));


        Reservation reservation = reservationRepository.findById(id_reservation)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'id : " + id_reservation));

        chambre.setStatut(ChambreStatut.OCCUPEE);
        chambre.setReservation(reservation);

        chambreRepository.save(chambre);
    }

    @Override
    public void setChambreDisponible(Long id) {

        Chambre chambre = chambreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chambre non trouvée avec l'id : " + id));

        chambre.setStatut(ChambreStatut.DISPONIBLE);
        chambre.setReservation(null);

        chambreRepository.save(chambre);
    }
}
