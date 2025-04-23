package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.ChambreDTO;
import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.ChambreReservation;
import com.api.apicheck_incheck_out.Enums.ChambreType;
import com.api.apicheck_incheck_out.Mapper.ChambreMapper;
import com.api.apicheck_incheck_out.Service.ChambreReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chambres")
public class ChambreReservationController {

    private final ChambreReservationService chambreReservationService;
    private final ChambreMapper chambreMapper;

    public ChambreReservationController(ChambreReservationService chambreReservationService, ChambreMapper chambreMapper) {
        this.chambreReservationService = chambreReservationService;
        this.chambreMapper = chambreMapper;
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ChambreDTO>> getChambresDisponibles() {
        List<Chambre> chambresDisponibles = chambreReservationService.getChambresDisponibles();

        List<ChambreDTO> chambreDTOList = chambresDisponibles.stream()
                .map(chambreMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(chambreDTOList, HttpStatus.OK);
    }

    @PutMapping("/occupee/{id}")
    public ResponseEntity<Void> setChambreOccupee(@RequestParam Long idReservation) {
        chambreReservationService.setChambreOccupee(idReservation);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/disponible/{id}")
    public ResponseEntity<Void> setChambreDisponible(@PathVariable Long id) {
        chambreReservationService.setChambreDisponible(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/reservation/{id}")
    public ResponseEntity<List<ChambreDTO>> getChambresByReservation(@PathVariable Long id) {
        List<Chambre> chambres = chambreReservationService.getChambresByReservation(id);

        List<ChambreDTO> chambreDTOList = chambres.stream()
                .map(chambreMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(chambreDTOList, HttpStatus.OK);
    }
    @PutMapping("/reservée/{id}")
    public ResponseEntity<Void> setChambreReserved(@PathVariable Long id,@RequestParam Long id_reservation){
        chambreReservationService.setChambreReserved(id,id_reservation);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/disponibles/filtre")
    public ResponseEntity<List<ChambreDTO>> getChambresDisponiblesAvecFiltre(
            @RequestParam String dateDebut,
            @RequestParam String dateFin,
            @RequestParam(required = false) Integer capacite,
            @RequestParam(required = false) ChambreType type,
            @RequestParam(required = false) String etage) {

        List<Chambre> chambres = chambreReservationService.findChambresDisponibles(dateDebut, dateFin, capacite, type, etage);
        List<ChambreDTO> chambreDTOList = chambres.stream()
                .map(chambreMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(chambreDTOList, HttpStatus.OK);
    }
}
