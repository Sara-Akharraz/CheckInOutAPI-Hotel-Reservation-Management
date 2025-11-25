package com.api.apicheck_incheck_out.controller;

import com.api.apicheck_incheck_out.dto.ChambreDTO;
import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.mapper.ChambreMapper;
import com.api.apicheck_incheck_out.service.ChambreReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
                .toList();

        return new ResponseEntity<>(chambreDTOList, HttpStatus.OK);
    }

    @PutMapping("/occupee/{id}")
    public ResponseEntity<Void> setChambreOccupee(@PathVariable Long id) {
        chambreReservationService.setChambreOccupee(id);
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
                .toList();

        return new ResponseEntity<>(chambreDTOList, HttpStatus.OK);
    }
    @PutMapping("/reservée/{id}")
    public ResponseEntity<Void> setChambreReserved(@PathVariable Long id,@RequestParam Long idReservation){
        chambreReservationService.setChambreReserved(id,idReservation);
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
                .toList();

        return new ResponseEntity<>(chambreDTOList, HttpStatus.OK);
    }
}
