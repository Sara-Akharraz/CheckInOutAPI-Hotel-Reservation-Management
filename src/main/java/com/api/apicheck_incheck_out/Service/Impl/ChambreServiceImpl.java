package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.Repository.ChambreRepository;
import com.api.apicheck_incheck_out.Service.ChambreService;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChambreServiceImpl implements ChambreService {

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public Chambre addChambre(Chambre chambre) {
        return chambreRepository.save(chambre);
    }

    @Override
    public Chambre updateChambre(Long id_chambre, Chambre updateChambre) {
       Optional<Chambre> chambre=chambreRepository.findById(id_chambre);
       if(chambre.isPresent()){
           Chambre existedChambre=chambre.get();
           existedChambre.setStatut(updateChambre.getStatut());
           existedChambre.setPrix(updateChambre.getPrix());
           existedChambre.setNom(updateChambre.getNom());
           existedChambre.setType(updateChambre.getType());
           existedChambre.setEtage(updateChambre.getEtage());
           existedChambre.setCapacite(updateChambre.getCapacite());
           existedChambre.setReservation(updateChambre.getReservation());

           chambreRepository.save(existedChambre);
           return existedChambre;

       }else{
           throw new RuntimeException("Chambre non trouvée avec l'id :" +id_chambre);
       }
    }

    @Override
    public void deleteChambre(Long id) {
        if(!chambreRepository.existsById(id)) {
            throw new RuntimeException("Chambre non trouvée avec l'id :"+id);
        }
        chambreRepository.deleteById(id);
    }

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

        return chambreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chambre non trouvée avec l'id : " + id));
    }

    @Override
    public List<Chambre> getChambresByReservation(Long id) {

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

        if(reservation.getStatus()==ReservationStatus.Confirmee){
            chambre.setStatut(ChambreStatut.OCCUPEE);
            chambre.setReservation(reservation);

            chambreRepository.save(chambre);
        }else{
            throw new RuntimeException("La réservation est non confirmée , le statut actuel :"+reservation.getStatus());
        }

    }

    @Override
    public void setChambreDisponible(Long id) {

        Chambre chambre = chambreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chambre non trouvée avec l'id : " + id));

        chambre.setStatut(ChambreStatut.DISPONIBLE);
        chambre.setReservation(null);

        chambreRepository.save(chambre);
    }

    @Override
    public void setChambreReserved(Long id, Long id_reservation) {
        Chambre chambre=chambreRepository.findById(id).orElseThrow(()->new RuntimeException("chambre non trouvée avec l'id :"+id));
        Reservation reservation=reservationRepository.findById(id_reservation).orElseThrow(()->new RuntimeException("Reservation non trouvée avec l'id "+id));
        if(reservation.getStatus()== ReservationStatus.En_Attente) {
            chambre.setStatut(ChambreStatut.RESERVED);
            chambre.setReservation(reservation);
            chambreRepository.save(chambre);
        }else{
            throw new RuntimeException("La réservation n'est pas en En attente, statut actuel: "+reservation.getStatus());
        }
    }
}
